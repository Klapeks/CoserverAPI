package com.klapeks.coserver.plugin.bungee;

import java.net.Socket;
import java.util.function.BiFunction;
import java.util.function.Function;

import com.klapeks.coserver.aConfig;

@Deprecated(forRemoval=true)
public class BungeeCoserv {

	
	public static void __init__() {
		if (SuperCoServer.BungeeCoserv==null) {
			SuperCoServer.BungeeCoserv = new SuperCoServer(aConfig.bungee.port);
		}
	}
	/**
	 * @param command
	 * @param func - request -> {return "response";}
	 */
	public static void addHandler(String command, Function<String, String> func) {
		SuperCoServer.BungeeCoserv.addHandler(command, func);
	}
	/**
	 * @param command
	 * @param func - request -> {return "response";}
	 */
	public static void addSecurityHandler(String command, Function<String, String> func) {
		SuperCoServer.BungeeCoserv.addSecurityHandler(command, func);
	}
	/**
	 * @param command
	 * @param func - request -> {return "response";}
	 */
	public static void addSocketHandler(String command, BiFunction<Socket, String, String> func) {
		SuperCoServer.BungeeCoserv.addSocketHandler(command, func);
	}
	/**
	 * @param command
	 * @param func - request -> {return "response";}
	 */
	public static void addSocketSecurityHandler(String command, BiFunction<Socket, String, String> func) {
		SuperCoServer.BungeeCoserv.addSocketSecurityHandler(command, func);
	}
	
}
