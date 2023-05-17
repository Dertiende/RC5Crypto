package main.utils;

import main.domain.RC5Data;
import main.model.RequestInfo;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class CryptoProviderRC5 {
	public static int sizeModW4;
	long size;
	int c, b, w, w4, w8, T, R;
	Long mod, mask;
	long[] L, S;
	byte[] key, keyAl, vector, lastBlock;


	public CryptoProviderRC5(RC5Data rc5Data) {
		this.w = rc5Data.getBsize();
		this.R = rc5Data.getRounds();
		this.key = rc5Data.getKey().getBytes(StandardCharsets.UTF_8);
		this.T = 2 * (R + 1);
		this.w4 = w / 4;
		this.w8 = w / 8;
		this.mod = CryptoUtils.pow(2, w);
		this.mask = mod - 1;
		this.b = key.length;
		size = rc5Data.getSize();
		sizeModW4 = w4-(int) (size % w4);
		lastBlock = Longs.toByteArray(rc5Data.getVector());
		vector = Arrays.copyOf(lastBlock, w4);
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
		return switch (w) {
			case 16 -> new BigInteger[]{new BigInteger("47073"),
					new BigInteger("40503")};
			case 32 -> new BigInteger[]{new BigInteger("3084996963"),
					new BigInteger("2654435769")};
			case 64 -> new BigInteger[]{new BigInteger("13249961062380153451"),
					new BigInteger("11400714819323198485")};
			default -> throw new IllegalStateException("Unexpected value: " + w + ". Available values: 16, 32, 64.");
		};
	}

	void keyAlign() {
		keyAl = new byte[b + (w8 - b % w8)];
		System.arraycopy(key, 0, keyAl, 0, key.length);
		if (b == 0) {
			c = 1;
		} else if (b % w8 != 0) {
			b = keyAl.length;
			c = b / w8;
		} else {
			c = b / w8;
		}
		long[] L = new long[c];

		for (int i = b - 1; i > -1; i--) {
			L[i / w8] = (L[i / w8] << 8) + Byte.toUnsignedInt(keyAl[i]);
		}
		this.L = L;
	}

	void keyExtend() {
		BigInteger[] PQ = constGen();
		BigInteger P = PQ[0];
		BigInteger Q = PQ[1];
		S = new long[T];
		for (int i = 0; i < T; i++) {
			S[i] = (Q.multiply(BigInteger.valueOf(i))).add(P).remainder(BigInteger.valueOf(mod)).longValue();
		}
	}

	void shuffle() {
		int i = 0;
		int j = 0;
		long A = 0;
		long B = 0;
		for (int k = 0; k < 3 * Math.max(c, T); k++) {
			A = S[i] = lshift((S[i] + A + B), 3);
			B = L[j] = lshift((L[j] + A + B), A + B);
			i = (i + 1) % T;
			j = (j + 1) % c;
		}
	}

	byte[] processBlock(byte[] data, boolean isEncrypt) {
		byte[] dataA = new byte[w8];
		byte[] dataB = new byte[w8];
		for (int i = 0; i < w8; i++) {
			dataA[dataA.length - i - 1] = data[i];
		}
		for (int i = 0; i < w8; i++) {
			dataB[dataB.length - i - 1] = data[w8 + i];
		}
		BigInteger A = new BigInteger(1, dataA);
		BigInteger B = new BigInteger(1, dataB);
		if (isEncrypt) {
			A = A.add(BigInteger.valueOf(S[0])).mod(BigInteger.valueOf(this.mod));
			B = B.add(BigInteger.valueOf(S[1])).mod(BigInteger.valueOf(this.mod));
			for (int i = 1; i <= this.R; i++) {
				A = BigInteger.valueOf((lshift((A.longValue() ^ B.longValue()), B.longValue()) + S[2 * i]) % this.mod);
				B = BigInteger.valueOf((lshift((A.longValue() ^ B.longValue()), A.longValue()) + S[2 * i + 1]) % this.mod);
			}
		} else {
			for (int i = this.R; i > 0; i--) {
				B = BigInteger.valueOf(rshift(B.longValue() - S[2 * i + 1], A.longValue()) ^ A.longValue());
				A = BigInteger.valueOf(rshift(A.longValue() - S[2 * i], B.longValue()) ^ B.longValue());
			}
			B = B.subtract(BigInteger.valueOf(S[1])).mod(BigInteger.valueOf(this.mod));
			A = A.subtract(BigInteger.valueOf(S[0])).mod(BigInteger.valueOf(this.mod));
		}
		dataA = CryptoUtils.reverse(A, w8);
		dataB = CryptoUtils.reverse(B, w8);
		return CryptoUtils.byteSum(dataA, dataB);
	}

	public Long encryptFile(RequestInfo info) throws IOException {
		int bufferSize;
		byte[] buffer, encoded, w4Array, hash;
		buffer = new byte[64000];
		bufferSize = 64000;
		encoded = new byte[64000];
		FileInputStream inputStream = new FileInputStream(info.inputFilePath());

		long startTime = System.currentTimeMillis();
		long finishTime = 0;
		createOutFile(info.outputFilePath());
		FileOutputStream outputStream = new FileOutputStream(info.outputFilePath());
		hash =  Longs.toByteArray(CryptoUtils.getCRC32(info.inputFilePath()));
		byte[] tmp = hash.clone();
		hash = new byte[4];
		System.arraycopy(tmp,4,hash,0,4);
		outputStream.write(hash);
		while (inputStream.available() > 0) {
			info.progressListener().update((long)inputStream.available(), size);
			if (inputStream.available() < bufferSize) {
				bufferSize = inputStream.available() + (w4 - bufferSize % w4);
				buffer = new byte[bufferSize + (w4 - bufferSize % w4)];
				encoded = new byte[bufferSize + (w4 - bufferSize % w4)];
			}
//			noinspection ResultOfMethodCallIgnored
			inputStream.read(buffer, 0, bufferSize);
			for (int i = 0; i < bufferSize ; i += w4) {
				//System.out.println("print "+ i);
				w4Array = CryptoUtils.toW4Array(buffer, i, w4);
				w4Array = CryptoUtils.xor(w4Array, lastBlock);
				w4Array = processBlock(w4Array, true);
				lastBlock = Arrays.copyOf(w4Array, w4);
				System.arraycopy(w4Array, 0, encoded, i, w4);
			}
			outputStream.write(encoded);
			finishTime = System.currentTimeMillis();
		}
		inputStream.close();
		outputStream.close();
		return finishTime - startTime;

	}

	private static void createOutFile(String outFileName) {
		try {
			File f = new File(outFileName);
			if (!f.createNewFile()) {
				//noinspection ResultOfMethodCallIgnored
				f.delete();
				//noinspection ResultOfMethodCallIgnored
				f.createNewFile();
			}
			System.out.println("Output file created");
//			encryption.print("Output file created");
		} catch (Exception e) {
			System.out.println("Output file already exists");
		}
	}

	public Long decryptFile(RequestInfo info) throws IOException {
		int bufferSize;
		byte[] buffer, decoded, w4Array, tempArray, hash;
		hash = new byte[4];
		buffer = new byte[64000];
		bufferSize = 64000;
		decoded = new byte[64000];
		FileInputStream inputStream = new FileInputStream(info.inputFilePath());
		//noinspection ResultOfMethodCallIgnored
		inputStream.read(hash, 0, 4);
		//System.out.println("Read hash: "+ new BigInteger(hash));
		long startTime = System.currentTimeMillis();
		long finishTime = 0;
		createOutFile(info.outputFilePath());
		FileOutputStream outputStream = new FileOutputStream(info.outputFilePath());
		while (inputStream.available() > 0) {
			info.progressListener().update((long)inputStream.available(), size);
			decoded = new byte[decoded.length];
			if (inputStream.available() < bufferSize) {
				bufferSize = inputStream.available();
				buffer = new byte[bufferSize];
				decoded = new byte[(int) (size % 64000)];
			}
			//noinspection ResultOfMethodCallIgnored
			inputStream.read(buffer, 0, bufferSize);
			for (int i = 0; i < bufferSize; i += w4) {
				//System.out.println("print "+ i);
				w4Array = CryptoUtils.toW4Array(buffer, i, w4);
				tempArray = Arrays.copyOf(w4Array, w4);
				w4Array = processBlock(w4Array, false);
				w4Array = CryptoUtils.xor(w4Array, lastBlock);
				lastBlock = Arrays.copyOf(tempArray, w4);
				if (decoded.length > i + w4) {
					System.arraycopy(w4Array, 0, decoded, i, w4);
				} else {
					System.arraycopy(w4Array, 0, decoded, i, decoded.length - i);
					break;
				}
			}
			outputStream.write(decoded);
			finishTime = System.currentTimeMillis();
		}
		inputStream.close();
		outputStream.close();
		return finishTime - startTime;
	}
}
