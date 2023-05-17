package main.utils;


public class Longs {

	public static byte[] toByteArray(long value) {
		byte[] result = new byte[8];

		for(int i = 7; i >= 0; --i) {
			result[i] = (byte)((int)(value & 255L));
			value >>= 8;
		}

		return result;
	}

	public static long fromByteArray(byte[] bytes) {
		checkArgument(bytes.length >= 8, "array too small: %s < %s", bytes.length, 8);
		return fromBytes(bytes[0], bytes[1], bytes[2], bytes[3], bytes[4], bytes[5], bytes[6], bytes[7]);
	}

	public static void checkArgument(boolean b, String errorMessageTemplate, int p1, int p2) {
		if (!b) {
			throw new IllegalArgumentException(errorMessageTemplate+p1+" "+p2);
		}
	}

	public static long fromBytes(byte b1, byte b2, byte b3, byte b4, byte b5, byte b6, byte b7, byte b8) {
		return ((long)b1 & 255L) << 56 | ((long)b2 & 255L) << 48 | ((long)b3 & 255L) << 40 | ((long)b4 & 255L) << 32 | ((long)b5 & 255L) << 24 | ((long)b6 & 255L) << 16 | ((long)b7 & 255L) << 8 | (long)b8 & 255L;
	}
}
