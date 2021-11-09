package main;

import sqlite.sqliteDB;

import java.io.*;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Scanner;
import sqlite.sqliteDB;

public class rc5 {
    rc5Obj cli;
    public static int eof = 0;
    public static int sizeModW4;
    public static long size = 0;
    int c, b,w,w4,w8,T,R;
    Long  mod,mask;
    long[] L,S;
    byte[] key, keyAl,vector,lastBlock;
    Long vec;

    public rc5(rc5Obj cli) {
        this.cli = cli;
        this.w = Integer.parseInt(cli.bsize);
        this.R = Integer.parseInt(cli.rounds);
        this.key = cli.key.getBytes(StandardCharsets.UTF_8);
        this.T = 2 * (R + 1);
        this.w4 = w / 4;
        this.w8 = w / 8;
        this.mod = utils.pow(2,w);
        this.mask = mod-1;
        this.b = key.length;
        size = Long.parseLong(cli.size);
        sizeModW4 = (int) (size % w4);
        vec = Long.parseLong(cli.vector);
        String vecs = cli.vector;
        lastBlock = BigInteger.valueOf(Long.parseLong(cli.vector)).toByteArray();
        vector = Arrays.copyOf(lastBlock,w4);
        this.keyAlign();
        this.keyExtend();
        this.shuffle();
    }

    long lshift(long val, long n) {
        n %= w;
        return ((val << n) & mask) | ((val & mask) >> (w - n));
    }


    long rshift(long val, long n) {
        n %= w;
        return ((val & mask) >> n) | (val << (w - n) & mask);
    }

    BigInteger[] constGen() {
        BigInteger[] pair;
        switch (w) {
            case 16:
                pair = new BigInteger[]{new BigInteger("47073"),
                                        new BigInteger("40503")};
                break;
            case 32:
                pair = new BigInteger[]{new BigInteger("3084996963"),
                                        new BigInteger("2654435769")};
                break;
            case 64:
                pair = new BigInteger[]{new BigInteger("13249961062380153451"),
                                        new BigInteger("11400714819323198485")};
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + w + ". Available values: 16, 32, 64.");
        }
        return pair;
    }

    void keyAlign(){
        keyAl = new byte[b+ (w8 - b % w8)];
        System.arraycopy(key, 0, keyAl, 0, key.length);
        if (b == 0){
            c = 1;
        }
        else if (b % w8 != 0 ){
            b = keyAl.length;
            c = b / w8;
        }
        else{
            c = b / w8;
        }
        long[] L = new long[c];

        for (int i = b-1; i > -1 ; i--) {
            L[i / w8] = (L[i / w8] << 8) + Byte.toUnsignedInt(keyAl[i]);
        }
        this.L = L;
    }

    void keyExtend(){
        BigInteger[] PQ = constGen();
        BigInteger P = PQ[0];
        BigInteger Q = PQ[1];
        S = new long[T];
        for (int i = 0; i < T; i++) {
            S[i] =(Q.multiply(BigInteger.valueOf(i))).add(P).remainder(BigInteger.valueOf(mod)).longValue();
        }
    }

    void shuffle(){
        int i = 0;
        int j = 0;
        long A = 0;
        long B = 0;
        for (int k = 0; k < 3 * Math.max(c,T) ; k++) {
            A = S[i] = lshift((S[i]+A+B), 3);
            B = L[j] = lshift((L[j]+A+B),A + B);
            i = (i+1) % T;
            j = (j+1) % c;
        }
    }


    byte[] encryptBlock(byte[] data){
        byte[] dataA = new byte[w8];
        byte[] dataB = new byte[w8];
        for (int i = 0; i < w8; i++) {
            dataA[dataA.length-i-1] = data[i];
        }
        for (int i = 0; i < w8; i++) {
            dataB[dataB.length-i-1] = data[w8+i];
        }
        BigInteger A = new BigInteger(1,dataA);
        BigInteger B = new BigInteger(1, dataB);
        A = A.add(BigInteger.valueOf(S[0])).mod(BigInteger.valueOf(this.mod));
        B = B.add(BigInteger.valueOf(S[1])).mod(BigInteger.valueOf(this.mod));

        for (int i = 1; i <=this.R ; i++) {
            A = BigInteger.valueOf ((lshift((A.longValue() ^ B.longValue()),B.longValue()) + S[2*i]) % this.mod);
            B = BigInteger.valueOf ((lshift((A.longValue() ^ B.longValue()),A.longValue()) + S[2*i+1]) % this.mod);
        }
        dataA = utils.reverse(A,w8);
        dataB = utils.reverse(B,w8);

        return utils.byteSum(dataA,dataB);
    }

    byte[] decryptBlock(byte[] data){
        byte[] dataA = new byte[w8];
        byte[] dataB = new byte[w8];
        for (int i = 0; i < w8; i++) {
            dataA[dataA.length-i-1] = data[i];
        }
        for (int i = 0; i < w8; i++) {
            dataB[dataB.length-i-1] = data[w8+i];
        }
        BigInteger A = new BigInteger(1,dataA);
        BigInteger B = new BigInteger(1, dataB);

        for (int i = this.R; i > 0 ; i--) {
            B = BigInteger.valueOf (rshift(B.longValue()- S[2*i+1],A.longValue()) ^ A.longValue());
            A = BigInteger.valueOf (rshift(A.longValue()- S[2*i],B.longValue()) ^ B.longValue());
        }
        B = B.subtract(BigInteger.valueOf(S[1])).mod(BigInteger.valueOf(this.mod));
        A = A.subtract(BigInteger.valueOf(S[0])).mod(BigInteger.valueOf(this.mod));
        dataA = utils.reverse(A,w8);
        dataB = utils.reverse(B,w8);
        return utils.byteSum(dataA,dataB);
    }
    int  encryptFile(String inpFileName,String outFileName) throws IOException, SQLException {
        size = Files.size(Paths.get(inpFileName));
        sizeModW4 = (int) (size % w4);
        lastBlock = utils.vector(w4);
        vector = Arrays.copyOf(lastBlock,w4);
        int bufferSize;
        byte[] buffer, encoded, w4Array, hash;
        long newSize;
        if (size < 64000){
            if ((size % w4) != 0){
                //System.out.println("size before "+ size);
                newSize = size+ w4-(size%w4);
                //System.out.println("size after "+ newSize);
            }
            else newSize = size;
            buffer = new byte[(int) newSize];
            bufferSize = (int) newSize;
            encoded = new byte[(int) newSize];
        }
        else{
            buffer = new byte[64000];
            bufferSize = 64000;
            encoded = new byte[64000];
        }
        //System.out.println("size "+ size);
        FileInputStream inputStream = new FileInputStream(inpFileName);
        long startTime = System.currentTimeMillis();
        long finishTime = 0;

        createOutFile(outFileName);
        Path path = Paths.get(outFileName);
        hash = BigInteger.valueOf(utils.getCRC32(inpFileName)).toByteArray();
        System.out.println("HashToFile: "+ Arrays.toString(hash));
        System.out.println("Longhash: "+ utils.getCRC32(inpFileName));
        Files.write(path,hash);
        while (inputStream.available() > 0) {
            if (inputStream.available() < bufferSize){
                bufferSize = inputStream.available();
                buffer = new byte[bufferSize + (bufferSize % w4)];
                encoded = new byte[bufferSize + (bufferSize % w4)];
                //noinspection ResultOfMethodCallIgnored
                inputStream.read(buffer, 0, bufferSize);
                System.out.println("Buffer"+ Arrays.toString(buffer));
            }
            else {
                //noinspection ResultOfMethodCallIgnored
                inputStream.read(buffer, 0, bufferSize);
            }

            for (int i = 0; i < bufferSize + (bufferSize % w4); i += w4) {
                //System.out.println("print "+ i);
                w4Array =  utils.toW4Array(buffer,i,w4);
                w4Array = utils.xor(w4Array,lastBlock);
                w4Array = encryptBlock(w4Array);
                lastBlock = Arrays.copyOf(w4Array,w4);
                System.arraycopy(w4Array,0,encoded,i,w4);
                //System.out.println(Arrays.toString(w4Array));
                //System.out.println("Ok");

            }

            Files.write(path,encoded, StandardOpenOption.APPEND);
            finishTime = System.currentTimeMillis();

        }
        inputStream.close();
        System.out.println("Encode: "+(finishTime-startTime)+" ms");
        sqliteDB.addFileToDB(utils.byteToLong(vector));
        return 0;
    }

    private static void createOutFile(String outFileName) {
        try {
            File f = new File(outFileName);
            if (f.createNewFile()){
                System.out.println("created file");
            }
            else{
                f.delete();
                f.createNewFile();
                System.out.println("created file");
            }
        }
        catch (Exception e){
            System.out.println("already exists");
        }
    }

    int  decryptFile(String inpFileName,String outFileName) throws IOException, SQLException {
        int bufferSize;
        byte[] buffer, decoded,hash = new byte[4];
        buffer = new byte[64000];
        bufferSize = 64000;
        decoded = new byte[64000];
        FileInputStream inputStream = new FileInputStream(inpFileName);
        //noinspection ResultOfMethodCallIgnored
        inputStream.read(hash,0,4);
        System.out.println("Read hash: "+ new BigInteger(hash));
        long startTime = System.currentTimeMillis();
        long finishTime = 0;
        byte[] w4Array;
        byte[] tempArray;
        lastBlock = Arrays.copyOf(vector,w4);
        createOutFile(outFileName);

        while (inputStream.available() > 0) {
            decoded = new byte[decoded.length];
            if (inputStream.available() < bufferSize){
                bufferSize = inputStream.available();
                buffer = new byte[bufferSize + (bufferSize % w4)];
                decoded = new byte[bufferSize- sizeModW4];
                //noinspection ResultOfMethodCallIgnored
                inputStream.read(buffer, 0, bufferSize);
                System.out.println("Buffer"+ Arrays.toString(buffer));
            }
            else {
                //noinspection ResultOfMethodCallIgnored
                inputStream.read(buffer, 0, bufferSize);
            }

            for (int i = 0; i < bufferSize; i += w4) {
                //System.out.println("print "+ i);
                eof = 0;
                w4Array =  utils.toW4Array(buffer,i,w4);

                tempArray = Arrays.copyOf(w4Array, w4);
                w4Array = decryptBlock(w4Array);
                w4Array = utils.xor(w4Array,lastBlock);
                lastBlock = Arrays.copyOf(tempArray,w4);
                if(decoded.length>i+w4){
                    System.arraycopy(w4Array,0,decoded,i,w4);
                }
                else {
                    //System.out.println("final"+i+" "+w4+" "+decoded.length);
                    System.arraycopy(w4Array,0,decoded,i,decoded.length-i);
                }
                //System.out.println(Arrays.toString(w4Array));
                //System.out.println("Ok");
            }
            //System.out.println("dec "+ decoded.length + "size "+(size));
            Path path = Paths.get(outFileName);
            Files.write(path,decoded, StandardOpenOption.APPEND);
            finishTime = System.currentTimeMillis();

        }
        inputStream.close();
        System.out.println("Decode: "+(finishTime-startTime)+" ms");
        return 0;
    }
}
