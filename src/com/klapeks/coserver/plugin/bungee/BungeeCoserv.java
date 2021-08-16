package com.klapeks.coserver.plugin.bungee;

import java.util.HashMap;
import java.util.function.Function;

import com.klapeks.coserver.aConfig;
import com.klapeks.coserver.dCoserverServer;
import com.klapeks.coserver.dFunctions;

public class BungeeCoserv {

	private static HashMap<String, Function<String, String>> handlers = new HashMap<>();
	private static HashMap<String, Function<String, String>> securityHandlers = new HashMap<>();
	/**
	 * @param command
	 * @param func - request -> {return "response";}
	 */
	public static void addHandler(String command, Function<String, String> func) {
		handlers.put(command, func);
	}
	/**
	 * @param command
	 * @param func - request -> {return "response";}
	 */
	public static void addSecurityHandler(String command, Function<String, String> func) {
		securityHandlers.put(command, func);
	}
	
	static dCoserverServer server;
	static void __init__() {
		server = new dCoserverServer(aConfig.bungee.port) {
			@Override
			public String handle(String request) {
				String cmd = request.split(" ")[0];
				request = request.replaceFirst(cmd+" ", "");
				if (handlers.containsKey(cmd)) {
					return handlers.get(cmd).apply(request);
				}
				return super.handle(request);
			}
			@Override
			public String securityHandle(String request) {
				String cmd = request.split(" ")[0];
				request = request.replaceFirst(cmd+" ", "");
				if (securityHandlers.containsKey(cmd)) {
					return securityHandlers.get(cmd).apply(request);
				}
				return super.securityHandle(request);
			}
			@Override
			public void onEnable() {
				dFunctions.log("§aBungee side server was enabled on port: §6" + aConfig.bungee.port);
			}
		};
	}
	
}
