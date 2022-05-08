package com.klapeks.coserver;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.security.KeyPair;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.klapeks.funcs.Async;
import com.klapeks.funcs.dRSA;

public class Coserver {
	static Random random = new Random();
	static List<Coserver> allCoservers = new ArrayList<>();
	public static Coserver cordServer() {
		List<Coserver> css = new ArrayList<>(allCoservers);
		while (!css.isEmpty()) {
//			Coserver cs = css.remove(random.nextInt(css.size()));
			Coserver cs = css.remove(0);
			if (cs.isFree()) return cs;
		}
		Coserver cs = newCordServer(true);
		cs.open();
		return cs;
	}
//	public static Coserver newCordServer() {
//		return newCordServer(true);
//	}
	public static Coserver newCordServer(boolean isAuto) {
		Coserver cs = new Coserver(aConfig.bukkit.ip, aConfig.bukkit.port);
		if (isAuto) allCoservers.add(cs);
		return cs;
	}
	
	private static long initTime = System.currentTimeMillis();
	static final String securitySplitor = "0x25G";
	private static KeyPair keyPair = null;
	
	private final String ip;
	private final int port;
	private Socket socket;
	private DataInputStream din;
	private DataOutputStream dout;
	private PublicKey bungeeKey;
	private Boolean isFree = true;
	private int lastUse;
	public Coserver(String ip, int port) {
		this.ip = ip;
		this.port = port;
		lastUse = time();
	}
	public void open() {
		if (socket!=null) close();
		dFunctions.debug("Opening socket " + ip + ":" + port);
		try {
			socket = new Socket(ip, port);
			din = new DataInputStream(socket.getInputStream());
			dout = new DataOutputStream(socket.getOutputStream());
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public Async<String> asyncSend(String msg) {
		return new Async<>(() -> send(msg));
	}
	public Async<String> asyncSecuritySend(String msg) {
		return new Async<>(() -> securitySend(msg));
	}
	public String send(boolean useSecurity, String cmd) {
		if (!useSecurity) return send(cmd);
		return securitySend(cmd);
	}
	public String send(String msg) {
		return send_raw(msg);
	}
	public String securitySend(String cmd) {
		if (keyPair==null) {
			keyPair = dRSA.generateKeyPair(aConfig.rsaKeySize);
		}
		if (bungeeKey==null) {
			String s = send(aConfig.securityKey + securitySplitor + "givemepswpls");
			if (s==null || s.equals("null") || s.equals("Errors 400/401/403/405/417/501/503 (go away lmao)")) {
				throw new RuntimeException("Security key '" + aConfig.securityKey + "' is incorrect.");
			}
			bungeeKey = dRSA.toPublicKey(s);
		}
		String r = send("enc/"+dRSA.fromKey(keyPair.getPublic())+"/dec/"+dRSA.rsaEncrypt(cmd, bungeeKey));
		if (r!=null && r.equals("Errors 400/401/403/405/417/501/503 (go away lmao)")) {
			bungeeKey=null;
			dFunctions.debug("§cerrored response -> try again");
			return securitySend(cmd);
		}
		return dRSA.rsaDecrypt(r, keyPair.getPrivate());
	}
	private boolean isFree() {
//		if (isLocked) return false;
		synchronized (isFree) {
			return isFree;
		}
	}

	public void close() {
		dFunctions.debug("Closing socket " + ip + ":" + port);
		try {
			send("CloseConnection");
		} catch (Throwable t) {}
		try {
			if (socket!=null) socket.close();
			if (din!=null) din.close();
			if (dout!=null) dout.close();
		} catch (Throwable t) {
			t.printStackTrace();
		}
		socket = null;
		din = null;
		dout = null;
		allCoservers.remove(this);
	}
	private String send_raw(String cmd) {
		synchronized (isFree) {
			lastUse = time();
			isFree = false;
			try {
				boolean single = dout==null;
				if (single) open();
				dout.writeUTF("[Large]" + cmd); // Send command to server
				dout.flush();
				String response = din.readUTF(); // Get response from server
				if (response!=null && response.startsWith("[Large]Willbe:")) {
					int size = Integer.parseInt(response.split(":")[1]);
					response = "";
					for (int i = 0; i < size; i++) {
						response += din.readUTF();
					}
				}
				if (single) close();
				if (response==null||response.equals("404error")) return null;
				return response;
			} catch (Throwable e) {
				throw new RuntimeException(e);
			} finally {
				isFree = true;
			}
		}
	}
	public static void closeAll() {
		while (allCoservers.size() > 0) {
			allCoservers.remove(0).close();
		}
	}
	public static void every5MinutesCheck() {
		List<Coserver> css = new ArrayList<>(allCoservers);
		while(css.size()>1) {
			Coserver cs = css.remove(1);
			if (cs.isFree() && time() - cs.lastUse >= 60) {
				cs.close();
			}
		}
	}
	static int time() {
		return (int)((System.currentTimeMillis()-initTime)/1000);
	}
}
