package com.klapeks.coserver.plugin.bungee;

import java.net.Socket;
import java.util.HashMap;
import java.util.function.BiFunction;
import java.util.function.Function;

import com.klapeks.coserver.dCoserverServer;
import com.klapeks.coserver.dFunctions;

public class SuperCoServer extends dCoserverServer{

	public static SuperCoServer BungeeCoserv;
	
	
	private HashMap<String, Function<String, String>> handlers = new HashMap<>();
	private HashMap<String, Function<String, String>> securityHandlers = new HashMap<>();
	
	private HashMap<String, BiFunction<Socket, String, String>> bi_handlers = new HashMap<>();
	private HashMap<String, BiFunction<Socket, String, String>> bi_securityHandlers = new HashMap<>();
	/**
	 * @param command
	 * @param func - request -> {return "response";}
	 */
	public void addHandler(String command, Function<String, String> func) {
		handlers.put(command, func);
	}
	/**
	 * @param command
	 * @param func - request -> {return "response";}
	 */
	public void addSecurityHandler(String command, Function<String, String> func) {
		securityHandlers.put(command, func);
	}
	/**
	 * @param command
	 * @param func - request -> {return "response";}
	 */
	public void addSocketHandler(String command, BiFunction<Socket, String, String> func) {
		bi_handlers.put(command, func);
	}
	/**
	 * @param command
	 * @param func - request -> {return "response";}
	 */
	public void addSocketSecurityHandler(String command, BiFunction<Socket, String, String> func) {
		bi_securityHandlers.put(command, func);
	}
	
	public SuperCoServer(int port) {
		super(port);
	} 
	
	@Override
	public String handle(Socket socket, String request) {
		String cmd = request.split(" ")[0];
		if (bi_handlers.containsKey(cmd)) {
			return bi_handlers.get(cmd).apply(socket, request.replaceFirst(cmd+" ", ""));
		}
		return super.handle(socket, request);
	}
	@Override
	public String securityHandle(Socket socket, String request) {
		String cmd = request.split(" ")[0];
		if (bi_securityHandlers.containsKey(cmd)) {
			return bi_securityHandlers.get(cmd).apply(socket, request.replaceFirst(cmd+" ", ""));
		}
		return super.securityHandle(socket, request);
	}
	@Override
	public String handle(String request) {
		String cmd = request.split(" ")[0];
		if (handlers.containsKey(cmd)) {
			return handlers.get(cmd).apply(request.replaceFirst(cmd+" ", ""));
		}
		return super.handle(request);
	}
	@Override
	public String securityHandle(String request) {
		String cmd = request.split(" ")[0];
		if (securityHandlers.containsKey(cmd)) {
			return securityHandlers.get(cmd).apply(request.replaceFirst(cmd+" ", ""));
		}
		return super.securityHandle(request);
	}
	
	@Override
	public void onEnable() {
		dFunctions.log("§aSuperCoServer was enabled on port: §6" + port);
	}

	
}
