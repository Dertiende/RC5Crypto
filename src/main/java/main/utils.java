package main;

import GUI.controllers.Encryption;
//import com.beust.jcommander.JCommander;
import java.io.*;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Random;
import java.util.zip.CRC32;


public class utils {
	static Encryption encryption;
	public static long pow(int a, int b) {
		long pow = 1;
		for (int i = 0; i < b; i++) pow *= a;

		return pow;
	}

	public static byte[] xor(byte[] b1, byte[] b2) {
		byte[] xor = new byte[b1.length];
		for (int i = 0; i < b1.length; i++) {
			xor[i] = (byte) (b1[i] ^ b2[i]);
		}
		return xor;
	}

	public static byte[] vector(int w4) {
		byte[] vector = new byte[w4];
		new Random().nextBytes(vector);
		return vector;
	}

	public static void getEncryption(Encryption encryption) {
		utils.encryption = encryption;
	}

	private static byte[] fillReversed(byte[] old, int w8) {
		byte[] newB = new byte[w8];
		int i = 0;
		while (i < old.length) {
			newB[i + w8 - old.length] = old[i];
			i++;
		}
		return newB;
	}

	public static byte[] reverse(BigInteger array, int w8) {
		byte[] data;
		if (array.toByteArray().length > w8) {
			data = Arrays.copyOfRange(array.toByteArray(), 1, array.toByteArray().length);
		} else {
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


	public static byte[] byteSum(byte[] b1, byte[] b2) {
		byte[] sum = new byte[b1.length + b2.length];
		for (int i = 0; i < sum.length; i++) {
			if (i < b1.length) {
				sum[i] = b1[i];
			} else {
				sum[i] = b2[i - (b1.length)];
			}
		}
		return sum;
	}

	public static byte[] toW4Array(byte[] data, int lust, int w4) {
		byte[] w4Array = new byte[w4];
		System.arraycopy(data, lust, w4Array, 0, w4);
		return w4Array;
	}

//	private static void cliParse(rc5Obj cli , String[] args) {
//		JCommander jCommander =  JCommander.newBuilder()
//				.addObject(cli)
//				.build();
//		if (args.length == 0){
//			jCommander.usage();
//			System.out.println("Press Any Key To Continue...");
//			new java.util.Scanner(System.in).nextLine();
//			System.exit(0);
//		}
//		else{
//			jCommander.parse(args);
//		}
//
//	}

	public static rc5Obj getCLI(String[] args) throws IOException {
		rc5Obj cli = new rc5Obj();
		//cliParse(cli,args);
		cli.size = String.valueOf(Files.size(Paths.get(cli.input)));
		byte[] vectorB = vector(Integer.parseInt(cli.bsize)/4);
		cli.vector = String.valueOf(Longs.fromByteArray(vectorB));
		return cli;
	}

	public static long getCRC32(String filepath) throws IOException {

		InputStream inputStream = new BufferedInputStream(new FileInputStream(filepath));
		CRC32 crc = new CRC32();
		int cnt;
		while ((cnt = inputStream.read()) != -1) {
			crc.update(cnt);
		}
		return crc.getValue();
	}

	public static void getDecodeInfo(rc5Obj cli,String path) throws IOException {
		byte[] hash = new byte[4];
		FileInputStream inputStream = new FileInputStream(path);
		//noinspection ResultOfMethodCallIgnored
		inputStream.read(hash,0,4);
		inputStream.close();
		cli.hash = String.valueOf(fromBytes(hash[0],hash[1],hash[2],hash[3]));
		int k = 0;

	}
	public static long fromBytes(byte b1, byte b2, byte b3, byte b4) {
		return (b1 & 255L) << 24 | (b2 & 255L) << 16 | (b3 & 255L) << 8 | b4 & 255L;
	}

	public static void isHashCorrect(long h1, long h2){
		if (h1 == h2){
			encryption.print("File integrity verified");
			//System.out.println("File integrity verified.");
		}
		else{
			encryption.print("Integrity check fails");
			//System.out.println("Integrity check fails.");
		}
	}

	public static class subscriberDTO{
		public String name;
		public Long value;
		subscriberDTO(String name, Long value){
			this.name = name;
			this.value = value;
		}
	}
}
