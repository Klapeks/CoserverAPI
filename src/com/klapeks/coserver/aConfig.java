package com.klapeks.coserver;

public class aConfig {

	public static String securityKey = "SomeTestSecurityKey";
	
	public static int rsaKeySize = 1024;
	
	public static boolean useDebugMsg = false;

	public static boolean useSecurity = false;

	public static boolean shutdownOnError = false;

	public static class bukkit {
		public static int port = -1;
		public static String ip = "0.0.0.0";
	}
	public static class bungee {
		public static int port = -1;
	}
	
	
}
