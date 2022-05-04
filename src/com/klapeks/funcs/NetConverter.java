package com.klapeks.funcs;

import java.util.Random;

public class NetConverter {
	
	static String randomAcc = "ghijklmnopqrstuvwxyz";
	
	public static String encode(String str, int deltaTime, int deltaSpeed) {
		byte[] arr = java.util.Base64.getEncoder().encode(str.getBytes());
		String result = "av";
		Random rand = new Random(System.currentTimeMillis());
		for (int i = sum(deltaTime); i>0;i--) {
			result += Character.toString(randomAcc.charAt((int)(rand.nextInt(randomAcc.length()))));
		}
		for (int i = 0; i < arr.length; i++) {
			String e = Integer.toString(arr[i] + deltaSpeed, 16);
			if (e.length()>3) return str;
			while(e.length()<3) e = randomAcc.charAt((int)(rand.nextInt(randomAcc.length())))+e;
			result += e;
		}
		return result;
	}
	public static String decode(String str, int deltaTime, int deltaSpeed) {
		if (!str.startsWith("av")) throw new RuntimeException("DECODE ERROR");
		str = str.substring(2).substring(sum(deltaTime));
		String r = "";
		for (int i = 0; i < str.length(); i+=3) {
			String e = str.substring(i, i+3);
			while (randomAcc.contains(Character.toString(e.charAt(0)))) {
				e = e.substring(1);
			}
			r += Character.toString((char) (Integer.parseInt(e, 16)-deltaSpeed));
		}
		return new String(java.util.Base64.getDecoder().decode(r));
	}
	
	public static int sum(int s) {
		int e = 0;
		while (true) {
			e += s%10;
			s /= 10;
			if (s < 10) {
				e+=s;
				break;
			} 
		}
		return e;
	}


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
}
