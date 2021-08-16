package com.klapeks.coserver;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;
import java.util.Base64;
import java.util.Random;

import javax.crypto.Cipher;

public class dRSA {

	public static String base64_encode(String str) {
		return new String(java.util.Base64.getEncoder().encode(str.getBytes()));
	}
	public static String base64_decode(String str) {
		if (str==null || str.equals("null")) return null;
		return new String(java.util.Base64.getDecoder().decode(str));
	}

	public static String base64_encode_byte(byte[] bytes) {
		return new String(java.util.Base64.getEncoder().encode(bytes));
	}
	public static byte[] base64_decode_byte(String str) {
		if (str==null || str.equals("null")) return null;
		return java.util.Base64.getDecoder().decode(str);
	}

	public static String generateSecretKey(int count) {
		try {
			return getSecretKey(count);
		} catch (Throwable t) {
			Random r = new Random();
			int sc = dFunctions.getRandom(r, 100000, Integer.MAX_VALUE-10);
			int a = dFunctions.getRandom(r, 10, 20);
			for (int i = 0; i < a; i++) {
				sc = sc + dFunctions.getRandom(r, 1, 201)-1 - 100;
			}
			return Base64.getEncoder().encodeToString(Integer.toHexString(sc).replace("\r", "").replace("\n", "").getBytes()).replace("=", "");
		}
	}
	private static String getSecretKey(int count) throws Throwable {
		String text = "https://www.passwordrandom.com/query?command=password&count="+count;

		InputStream is = new URL(text).openStream();
		BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
		{
			StringBuilder sb = new StringBuilder();
			int cp;
			while ((cp = rd.read()) != -1) {
				sb.append((char) cp);
			}
			text = sb.toString();
		}
		text = Base64.getEncoder().encodeToString(text.replace("\r", "").replace("\n", "").getBytes()).replace("=", "");
		return text;
	}
	
	public static String rsaEncrypt(String str, PublicKey publicKey) {
		try {
			Cipher encryptCipher = Cipher.getInstance("RSA");
			encryptCipher.init(Cipher.ENCRYPT_MODE, publicKey);
			byte[] bytes = str.getBytes(StandardCharsets.UTF_8);
			
			int iqii = publicKey.getEncoded().length / 2;
			String s = bytes.length+"";
			
			for (int a = 0; a <= bytes.length; a = a + iqii) {
				byte[] na = Arrays.copyOfRange(bytes, a, (a+iqii) > bytes.length ? bytes.length : (a + iqii));
				na = encryptCipher.doFinal(na);
				s = s + "%%%" + java.util.Base64.getEncoder().encodeToString(na);
			}
			return s;
		} catch (Throwable e) {
			throw new RuntimeException(e);
		}
	}
	public static String rsaDecrypt(String str, PrivateKey privateKey) {
		try {
			Cipher decryptCipher = Cipher.getInstance("RSA");
			decryptCipher.init(Cipher.DECRYPT_MODE, privateKey);
			byte[] bytes = new byte[dFunctions.toInt(str.split("%%%")[0])];
			str = str.replaceFirst(bytes.length+"%%%", "");
			int i = 0;
			for (String s : str.split("%%%")) {
				byte[] na = java.util.Base64.getDecoder().decode(s);
				na = decryptCipher.doFinal(na);
				for (byte b : na) {
					bytes[i++] = b;
				}
			}
			return new String(bytes, StandardCharsets.UTF_8);
		} catch (Throwable e) {
			throw new RuntimeException(e);
		}
	}
	
	public static KeyPair generateKeyPair(int size) {
		try {
			KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
			generator.initialize(size);
			return generator.generateKeyPair();
		} catch (Throwable e) {
			throw new RuntimeException(e);
		}
	}
	public static String fromKey(Key key) {
		return java.util.Base64.getEncoder().encodeToString(key.getEncoded());
	}
	public static PublicKey toPublicKey(String k) {
		return toPublicKey(java.util.Base64.getDecoder().decode(k));
	}
	public static PrivateKey toPrivateKey(String k) {
		return toPrivateKey(java.util.Base64.getDecoder().decode(k));
	}
	public static PublicKey toPublicKey(byte[] b) {
		try {
			KeyFactory keyFactory = KeyFactory.getInstance("RSA");
			return keyFactory.generatePublic(new X509EncodedKeySpec(b));
		} catch (Throwable e) {
			throw new RuntimeException(e);
		}
	}
	public static PrivateKey toPrivateKey(byte[] b) {
		try {
			KeyFactory keyFactory = KeyFactory.getInstance("RSA");
			return keyFactory.generatePrivate(new PKCS8EncodedKeySpec(b));
		} catch (Throwable e) {
			throw new RuntimeException(e);
		}
	}
}
