package com.klapeks.coserver.plugin.bungee;

import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.function.Function;

import com.klapeks.coserver.dCoserverServer;
import com.klapeks.coserver.dFunctions;

public class SuperCoServer extends dCoserverServer{

	public static SuperCoServer BungeeCoserv;
	

	private HashMap<String, Function<String, String>> handlers = new HashMap<>();
	private HashMap<String, Function<String, String>> securityHandlers = new HashMap<>();
	private HashMap<String, Function<String, String>> networkHandlers = new HashMap<>();
	
	private HashMap<String, BiFunction<Socket, String, String>> bi_handlers = new HashMap<>();
	private HashMap<String, BiFunction<Socket, String, String>> bi_securityHandlers = new HashMap<>();
	private HashMap<String, BiFunction<Socket, String, String>> bi_networkHandlers = new HashMap<>();
	private List<BiPredicate<Socket, Exception>> errorHandlers = new ArrayList<>();
	private List<Consumer<Socket>> closeHandlers = new ArrayList<>();
	
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
	public void addNetworkHandler(String command, Function<String, String> func) {
		networkHandlers.put(command, func);
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
	/**
	 * @param command
	 * @param func - request -> {return "response";}
	 */
	public void addNetworkSecurityHandler(String command, BiFunction<Socket, String, String> func) {
		bi_networkHandlers.put(command, func);
	}
	public void addCloseHandler(Consumer<Socket> func) {
		closeHandlers.add(func);
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
	public String networkHandle(Socket socket, String request) {
		String cmd = request.split(" ")[0];
		if (bi_networkHandlers.containsKey(cmd)) {
			return bi_networkHandlers.get(cmd).apply(socket, request.replaceFirst(cmd+" ", ""));
		}
		return super.networkHandle(socket, request);
	}
	@Override
	public String networkHandle(String request) {
		String cmd = request.split(" ")[0];
		if (networkHandlers.containsKey(cmd)) {
			return networkHandlers.get(cmd).apply(request.replaceFirst(cmd+" ", ""));
		}
		return super.networkHandle(request);
	}
	@Override
	public void onLargeClose(Socket socket) {
		closeHandlers.forEach(c -> c.accept(socket));
	}

	public void addErrorHandler(BiPredicate<Socket, Exception> f) {
		errorHandlers.add(f);
	}
	@Override
	public boolean handleError(Socket s, Exception e) {
		for (BiPredicate<Socket, Exception> b : errorHandlers) {
			if (b.test(s, e)) return true;
		}
		return false;
	}
	
	@Override
	public void onEnable() {
		dFunctions.log("§aSuperCoServer was enabled on port: §6" + port);
	}

	
}
