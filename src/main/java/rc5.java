import java.io.*;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

public class rc5 {
    public static int eof = 0;
    public static int sizeModW4;
    public static long size = 0;
    int c, b,w,w4,w8,T,R;
    Long  mod,mask;
    long[] L,S;
    byte[] key, keyAl;
    byte[] vector;
    byte[] lastBlock;
    byte[] b1;



    public rc5(int w, int R, byte[] key) {
        this.w = w;
        this.R = R;
        this.key = key;
        this.T = 2 * (R + 1);
        this.w4 = w / 4;
        this.w8 = w / 8;
        this.mod = utils.pow(2,w);
        this.mask = mod-1;
        this.b = key.length;
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
        BigInteger[] PQ = utils.constGen(w);
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
        long Al =A.longValue();
        long Bl = B.longValue();
        Al = Long.remainderUnsigned(Al + S[0],this.mod);
        Bl = Long.remainderUnsigned(Bl + S[1], this.mod);
        for (int i = 1; i <=this.R ; i++) {
            Al = Long.remainderUnsigned(lshift((Al ^ Bl),Bl) + S[2*i], this.mod);
            Bl = Long.remainderUnsigned(lshift((Al ^ Bl),Al) + S[2*i+1], this.mod);
        }
        dataA = utils.reverse(BigInteger.valueOf(Al),w8);
        dataB = utils.reverse(BigInteger.valueOf(Bl),w8);
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
        long Al =A.longValue();
        BigInteger B = new BigInteger(1, dataB);
        long Bl = B.longValue();

        for (int i = this.R; i > 0 ; i--) {
            Bl = rshift(Bl-S[2*i+1],Al) ^ Al;
            Al = rshift(Al-S[2*i],Bl) ^ Bl;
        }
        Bl = Long.remainderUnsigned(Bl-S[1],this.mod);
        Al = Long.remainderUnsigned (Al-S[0], this.mod);
        dataA = utils.reverse(BigInteger.valueOf(Al),w8);
        dataB = utils.reverse(BigInteger.valueOf(Bl),w8);

        return utils.byteSum(dataA,dataB);
    }
    int  encryptFile(String inpFileName,String outFileName) throws IOException {
        size = Files.size(Paths.get(inpFileName));
        sizeModW4 = (int) (w4-size % w4);
        System.out.println("size "+ size % w4 + " "+ size);
        lastBlock = utils.genBytes(w4);
        vector = Arrays.copyOf(lastBlock,w4);
        int bufferSize;
        byte[] buffer, encoded;
        long newSize;
        if (size < 64000){
            if ((size % w4) != 0){
                newSize = size+ w4-(size%w4);
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
        FileInputStream inputStream = new FileInputStream(inpFileName);
        FileOutputStream outputStream = new FileOutputStream(outFileName);
        FileChannel outChannel = outputStream.getChannel();
        FileLock lock = outChannel.lock();
        long startTime = System.currentTimeMillis();
        long finishTime = 0;
        byte[] w4Array;

        while (inputStream.available() > 0) {
            if (inputStream.available() < bufferSize){
                bufferSize = inputStream.available();
                buffer = new byte[bufferSize + w4-(bufferSize % w4)];
                encoded = new byte[bufferSize + w4-(bufferSize % w4)];
                //noinspection ResultOfMethodCallIgnored
                inputStream.read(buffer, 0, bufferSize);
            }
            else {
                //noinspection ResultOfMethodCallIgnored
                inputStream.read(buffer, 0, bufferSize);
            }
            try {
                File f = new File("D:\\Users\\Alex\\IdeaProjects\\kriptolab\\out.enc");
                if (f.createNewFile()){
                    System.out.println("created");
                }
            }
            catch (Exception e){
                System.out.println("already exists");
            }
            for (int i = 0; i < bufferSize + (bufferSize % w4) ; i += w4) {
                w4Array =  utils.toW4Array(buffer,i,w4);

                w4Array = utils.xor(w4Array,lastBlock);
                w4Array = encryptBlock(w4Array);
                lastBlock = Arrays.copyOf(w4Array,w4);
                System.arraycopy(w4Array,0,encoded,i,w4);

            }
            outChannel.write(ByteBuffer.wrap(encoded));
            finishTime = System.currentTimeMillis();

        }
        outChannel.close();
        System.out.println("Encode: "+(finishTime-startTime)/1000+" s");
        return 0;
    }

    int  decryptFile(String inpFileName,String outFileName) throws IOException {
        int bufferSize;
        byte[] buffer, decoded;
        long newSize;
        if (size < 64000){
            if ((size % w4) != 0){
                newSize = size+ w4-(size%w4);
            }
            else newSize = size;
            buffer = new byte[(int) newSize];
            bufferSize = (int) newSize;
            decoded = new byte[(int) size];
        }
        else{
            buffer = new byte[64000];
            bufferSize = 64000;
            decoded = new byte[64000];
        }
        FileInputStream inputStream = new FileInputStream(inpFileName);
        FileOutputStream outputStream = new FileOutputStream(outFileName);
        FileChannel outChannel = outputStream.getChannel();
        FileLock lock = outChannel.lock();
        long startTime = System.currentTimeMillis();
        long finishTime = 0;
        byte[] w4Array;
        byte[] tempArray;
        lastBlock = Arrays.copyOf(vector,w4);
        while (inputStream.available() > 0) {
            decoded = new byte[decoded.length];
            if (inputStream.available() < bufferSize){
                bufferSize = inputStream.available();
                buffer = new byte[bufferSize + (bufferSize % w4)];
                decoded = new byte[bufferSize- sizeModW4];
                //noinspection ResultOfMethodCallIgnored
                inputStream.read(buffer, 0, bufferSize);
            }
            else {
                //noinspection ResultOfMethodCallIgnored
                inputStream.read(buffer, 0, bufferSize);
            }

            for (int i = 0; i < bufferSize; i += w4) {
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
                    System.arraycopy(w4Array,0,decoded,i,decoded.length-i);
                }}
            outChannel.write(ByteBuffer.wrap(decoded));
            finishTime = System.currentTimeMillis();
        }
        outputStream.close();
        System.out.println("Decode: "+(finishTime-startTime)/1000+" s");
        return 0;
    }
}
