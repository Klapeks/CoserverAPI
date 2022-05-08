package com.klapeks.coserver;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.security.KeyPair;
import java.security.PublicKey;
import java.util.HashMap;
import java.util.Map;

import com.klapeks.funcs.Async;
import com.klapeks.funcs.IntPair;
import com.klapeks.funcs.dRSA;

@Deprecated(forRemoval=true, since="2.0")
public class dCoserver_old {

	public static Async<String> asyncSend(String cmd, boolean isLarge) {
		return asyncSend(aConfig.bukkit.ip, aConfig.bukkit.port, cmd, isLarge);
	}
	public static Async<String> asyncSecuritySend(String cmd, boolean isLarge) {
		return asyncSecuritySend(aConfig.bukkit.ip, aConfig.bukkit.port, cmd, isLarge);
	}
	public static Async<String> asyncSend(String cmd) {
		return asyncSend(cmd, false);
	}
	public static Async<String> asyncSecuritySend(String cmd) {
		return asyncSecuritySend(cmd, false);
	}
	

	public static String send(String cmd, boolean isLarge) {
		return send(aConfig.bukkit.ip, aConfig.bukkit.port, cmd, isLarge);
	}
	public static String securitySend(String cmd, boolean isLarge) {
		return securitySend(aConfig.bukkit.ip, aConfig.bukkit.port, cmd, isLarge);
	}
	public static String send(String cmd) {
		return send(cmd, false);
	}
	public static String securitySend(String cmd) {
		return securitySend(cmd, false);
	}
	public static void closeLarge() {
		closeLarge(aConfig.bukkit.ip, aConfig.bukkit.port);
	}
	
	private static class dCos {
		public Socket socket;
		public DataInputStream din;
		public DataOutputStream dout;
		public dCos(Socket socket) {
			this.socket = socket;
			try {
				din = new DataInputStream(socket.getInputStream());
				dout = new DataOutputStream(socket.getOutputStream());
			} catch (Throwable e) {
				throw new RuntimeException(e);
			}
		}
		public String send(String msg) {
			try {
				dout.writeUTF("[Large]"+msg); // Send command to server
				dout.flush();
				String response = din.readUTF(); // Get response from server
				if (response==null||response.equals("404error")) return null;
				if (response.startsWith("[Large]Willbe:")) {
					int size = Integer.parseInt(response.split(":")[1]);
					response = "";
					for (int i = 0; i < size; i++) {
						response += din.readUTF();
					}
				}
				return response;
			} catch (Throwable e) {
				throw new RuntimeException(e);
			}
		}
		public void close() {
			try {
				send("CloseConnection");
				din.close();
				dout.close();
				socket.close();
			} catch (Throwable t) {
				throw new RuntimeException(t);
			}
		}
	}

	
	private static Map<String, dCos> ip$socket = new HashMap<>(); 
	public static void closeLarge(String ip, int port) {
		if (ip$socket.containsKey(ip+"_"+port)) ip$socket.remove(ip+"_"+port).close();
	}
	

	public static Async<String> asyncSend(String ip, int port, String msg, boolean isLarge) {
		return new Async<>(() -> send(ip, port, msg, isLarge));
	}
	public static Async<String> asyncSecuritySend(String ip, int port, String cmd, boolean isLarge) {
		return new Async<>(() -> securitySend(ip, port, cmd, isLarge));
	}
	
	public static String send(String ip, int port, String msg, boolean isLarge) {
		try {
			if (isLarge) {
				if (!ip$socket.containsKey(ip+"_"+port)) {
					ip$socket.put(ip+"_"+port, new dCos(new Socket(ip, port)));
				}
				return ip$socket.get(ip+"_"+port).send(msg);
			}
			return send_raw(ip, port, msg);
		} catch (Throwable t) {
			throw new RuntimeException(t);
		}
	}
	
	private static String send_raw(String ip, int port, String cmd) {
		try {
			Socket s = new Socket(ip, port);
			DataInputStream din = new DataInputStream(s.getInputStream());
			DataOutputStream dout = new DataOutputStream(s.getOutputStream());
			dout.writeUTF(cmd); // Send command to server
			dout.flush();
			String response = din.readUTF(); // Get response from server
			if (response!=null && response.startsWith("[Large]Willbe:")) {
				int size = Integer.parseInt(response.split(":")[1]);
				response = "";
				for (int i = 0; i < size; i++) {
					response += din.readUTF();
				}
			}
			dout.close();
			s.close();
			if (response==null||response.equals("404error")) return null;
			return response;
		} catch (Throwable e) {
			throw new RuntimeException(e);
		}
	}

	static final String securitySplitor = "0x25G";
	private static Map<String, PublicKey> ip$password = new HashMap<>();
	private static KeyPair keyPair = null;
	public static String securitySend(String ip, int port, String cmd, boolean isLarge) {
		if (keyPair==null) {
			keyPair = dRSA.generateKeyPair(aConfig.rsaKeySize);
		}
		if (!ip$password.containsKey(ip+"_"+port)) {
			String s = send(ip, port, aConfig.securityKey + securitySplitor + "givemepswpls", isLarge);
			if (s==null || s.equals("null") || s.equals("Errors 400/401/403/405/417/501/503 (go away lmao)")) {
				throw new RuntimeException("Security key '" + aConfig.securityKey + "' is incorrect.");
			}
			ip$password.put(ip+"_"+port, dRSA.toPublicKey(s)); 
		}
		String r = send(ip, port, "enc/"+dRSA.fromKey(keyPair.getPublic())+"/dec/"+dRSA.rsaEncrypt(cmd, ip$password.get(ip+"_"+port)), isLarge);
		if (r!=null && r.equals("Errors 400/401/403/405/417/501/503 (go away lmao)")) {
			ip$password.remove(ip+"_"+port);
			dFunctions.debug("§cerrored response -> try again");
			return securitySend(ip, port, cmd, isLarge);
		}
		return dRSA.rsaDecrypt(r, keyPair.getPrivate());
//		String r = send(ip, port, aConfig.securityKey + ip$password.get(ip+"_"+port) + dCiphers.defaultCipher.encode(cmd));
//		if (r!=null && r.equals("Errors 400/401/403/405/417/501/503 (go away lmao)")) {
//			ip$password.clear();
//			return securitySend(ip, port, cmd);
//		}
//		return dCiphers.defaultCipher.decode(StringUtils.replaceOnce(r, aConfig.securityKey + ip$password.get(ip+"_"+port), ""));
	}
}
