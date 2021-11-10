package main;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Random;

public class keyGenerator {
	KeyGenerator key;
	SecretKey secretKey;
	String stringKey;
	final char[] lowercaseLatin = "abcdefghijklmnopqrstuvwxyz".toCharArray();
	final char[] uppercaseLatin = "ABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();
	final char[] lowercaseKirilic = "абвгдеёжзийклмнопрстуфхцчшщъыьэюя".toCharArray();
	final char[] uppercaseKirilic = "АБВГДЕЁЖЗИЙКЛМНОПРСТУФХЦЧШЩЪЫЬЭЮЯ".toCharArray();
	final char[] numbers = "0123456789".toCharArray();
	final char[] symbols = "+-=*/".toCharArray();
	final char[] allAllowed = ("abcdefghijklmnopqrstuvwxyz" +
			                           "ABCDEFGHIJKLMNOPQRSTUVWXYZ" +
			                           "абвгдеёжзийклмнопрстуфхцчшщъыьэюя" +
			                           "АБВГДЕЁЖЗИЙКЛМНОПРСТУФХЦЧШЩЪЫЬЭЮЯ" +
			                           "0123456789" +
			                           "+-=*/^?!@").toCharArray();

	public keyGenerator() {

	}

	public String getKey() throws NoSuchAlgorithmException {
		key = KeyGenerator.getInstance("AES");
		key.init(256);
		secretKey = key.generateKey();
		stringKey = Base64.getEncoder().encodeToString(secretKey.getEncoded());
		return stringKey;
	}

	public String getLab3Key(){

		Random random = new SecureRandom();

		StringBuilder password = new StringBuilder();

		for (int i = 0; i < 26; i++) {
			char check = allAllowed[random.nextInt(allAllowed.length)];
			while (password.toString().endsWith(String.valueOf(check))){
				check = allAllowed[random.nextInt(allAllowed.length)];
			}
			password.append(check);
		}
		password.insert(random.nextInt(password.length()), lowercaseLatin[random.nextInt(lowercaseLatin.length)]);
		password.insert(random.nextInt(password.length()), uppercaseLatin[random.nextInt(uppercaseLatin.length)]);
		password.insert(random.nextInt(password.length()), lowercaseKirilic[random.nextInt(lowercaseKirilic.length)]);
		password.insert(random.nextInt(password.length()), uppercaseKirilic[random.nextInt(uppercaseKirilic.length)]);
		password.insert(random.nextInt(password.length()), numbers[random.nextInt(numbers.length)]);
		password.insert(random.nextInt(password.length()), symbols[random.nextInt(symbols.length)]);
        return password.toString();
	}

	public boolean isPassRelevant(String name,String pass){
		int counter = 0;
		int passIsName = 0;
		for (char i: name.toCharArray()){
			if (pass.contains(String.valueOf(i))) passIsName++;
		}
		counter += isContain(pass,lowercaseLatin);
		counter += isContain(pass,uppercaseLatin);
		counter += isContain(pass,lowercaseKirilic);
		counter += isContain(pass,uppercaseKirilic);
		counter += isContain(pass,numbers);
		counter += isContain(pass,symbols);
		if (pass.length() < 10 || passIsName == pass.length()) counter = 0;
		return counter == 6;

	}
	private int isContain(String pass, char[] c){
		for (char ch : c) {
			if (pass.contains(String.valueOf(ch))) {
				return 1;
			}
		}
		return 0;
	}
}
