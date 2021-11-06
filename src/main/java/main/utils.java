package main;

import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Random;

public class utils {
    public static long pow (int a, int b){
        long pow = 1;
        for (int i = 0; i < b; i++) pow *= a;

        return pow;
    }

    public static byte[] xor(byte[] b1, byte[] b2){
        byte[] xor = new byte[b1.length];
        for (int i = 0; i < b1.length; i++) {
            xor[i] = (byte) (b1[i] ^ b2[i]);
        }
        return xor;
    }

    public static byte[] vector(int w8){
        byte[] vector = new byte[w8];
        new Random().nextBytes(vector);
        System.out.println("vect"+ Arrays.toString(vector));
        return vector;
    }

    public static byte[] fill(byte[] old,int size){
        byte[] newB = new byte[size];
        System.arraycopy(old, 0, newB, 0, size);
        return newB;
    }

    private static byte[] fillReversed(byte[] old, int w8){
        byte[] newB = new byte[w8];
        for (int i = 0; i < old.length; i++) {
            newB[i+w8-old.length] = old[i];
        }
        return newB;
    }

    public static byte[] reverse(BigInteger array,int w8) {
        byte[] data = new byte[w8];
        if (array.toByteArray().length > w8) {
            data = Arrays.copyOfRange(array.toByteArray(), 1, array.toByteArray().length);
        }
        else{
            int B = array.intValue();
            byte[] newData = ByteBuffer.allocate(w8).putInt(B).array();
            data = fillReversed(array.toByteArray(), w8);
        }

        int i = 0;
        int j = data.length - 1;
        byte tmp;
        while (j > i) {
            tmp = data[j];
            data[j] = data[i];
            data[i] = tmp;
            j--;
            i++;
        }
        return data;
    }


    public static byte[] byteSum(byte[] b1, byte[] b2){
        byte[] sum = new byte[b1.length+b2.length];
        for (int i = 0; i < sum.length; i++) {
            if (i < b1.length){
                sum[i] = b1[i];
            }
            else{
                sum[i] = b2[i-(b1.length)];
            }
        }
        return sum;
    }

    public static byte[] toW4Array(byte[] data, int lust, int w4){
        byte[] w4Array = new byte[w4];
        byte length = data[lust];

        System.arraycopy(data,lust,w4Array,0,w4);
        return w4Array;
    }

    public static byte[] longToBytes(long x,int w8) {
        ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);
        buffer.putLong(x);
        byte[] buf = new byte[w8];
        System.arraycopy(buffer.array(),4,buf,0,w8);
        return buf;
    }


}
