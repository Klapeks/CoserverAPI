package com.klapeks.coserver.plugin.bungee;

import java.net.Socket;
import java.util.function.BiFunction;
import java.util.function.Function;

import com.klapeks.coserver.aConfig;

@Deprecated(forRemoval=true)
public class BungeeCoserv {

	
	static SuperCoServer coserver;
	public static void __init__() {
		if (SuperCoServer.BungeeCoserv==null) {
			SuperCoServer.BungeeCoserv = new SuperCoServer(aConfig.bungee.port);
		}
		coserver = SuperCoServer.BungeeCoserv;
	}
	/**
	 * @param command
	 * @param func - request -> {return "response";}
	 */
	public static void addHandler(String command, Function<String, String> func) {
		coserver.addHandler(command, func);
	}
	/**
	 * @param command
	 * @param func - request -> {return "response";}
	 */
	public static void addSecurityHandler(String command, Function<String, String> func) {
		coserver.addSecurityHandler(command, func);
	}
	/**
	 * @param command
	 * @param func - request -> {return "response";}
	 */
	public static void addSocketHandler(String command, BiFunction<Socket, String, String> func) {
		coserver.addSocketHandler(command, func);
	}
	/**
	 * @param command
	 * @param func - request -> {return "response";}
	 */
	public static void addSocketSecurityHandler(String command, BiFunction<Socket, String, String> func) {
		coserver.addSocketSecurityHandler(command, func);
	}
	
}
