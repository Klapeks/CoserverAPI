package com.klapeks.coserver;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.KeyPair;
import java.security.PublicKey;

import javax.crypto.BadPaddingException;

import com.klapeks.funcs.dRSA;

public class dCoserverServer {
	
	protected ServerSocket serverSocket;
	public final int port;
	private KeyPair keyPair;
	boolean disabled = false;
	public dCoserverServer(int port) {
		this.port = port;
		keyPair = dRSA.generateKeyPair(aConfig.rsaKeySize);
		new Thread() {
			@Override
			public void run() {
				try {
					serverSocket = new ServerSocket(dCoserverServer.this.port);
					onEnable();
					while (!disabled) {
						try {
							Socket s = serverSocket.accept();
							new Thread(new SocketProcessor(dCoserverServer.this, s)).start();
//							new SocketProcessor(s).run();
						} catch(Throwable th) {
							if (serverSocket.isClosed() && disabled) break;
							th.printStackTrace();
						}
					}
				} catch (Throwable throwable) {
					throwable.printStackTrace();
				}
			}
		}.start();
	}
	public void shutdown() {
		try {
			disabled = true;
			serverSocket.close();
		} catch (Throwable throwable) {
			throwable.printStackTrace();
		}
	}
	public void onEnable() {dFunctions.log("Coserver was started with port " + dCoserverServer.this.port);}
	
	public String handle(Socket socket, String request) {
		return handle(request);
	}
	public String securityHandle(Socket socket, String request) {
		return securityHandle(request);
	}
	
	public String handle(String request) {
		return "null";
	}
	public String securityHandle(String request) {
		return "null";
	}
	
//	private static Map<String, PublicKey> ip$password = new HashMap<>();
	private static class SocketProcessor implements Runnable {
		private Socket s;
		private dCoserverServer hs;
		public SocketProcessor(dCoserverServer hs, Socket s) {
			this.s = s;
			this.hs = hs;
		}
		private String handleRequest(Socket s, String request) {
			if (request.startsWith("enc/") || request.contains("/dec/")) {
				request = request.replaceFirst("enc/", "");
				PublicKey pk = dRSA.toPublicKey(request.split("/dec/")[0]);
				request = request.substring(request.split("/dec/")[0].length()+"/dec/".length());
				try {
					request = dRSA.rsaDecrypt(request, hs.keyPair.getPrivate());
				} catch (Throwable t) {
					return null;
				}
				return dRSA.rsaEncrypt(hs.securityHandle(s, request), pk);
			}
			
			else if (request.startsWith(aConfig.securityKey+dCoserver.securitySplitor)) {
				request = request.substring(request.split(dCoserver.securitySplitor)[0].length()+dCoserver.securitySplitor.length());
				if (request.startsWith("givemepswpls")) {
					return dRSA.fromKey(hs.keyPair.getPublic());
				}
			}
			
			return hs.handle(s, request);
		}
		@Override
		public void run() {
			try {
				DataInputStream din = new DataInputStream(s.getInputStream());
				DataOutputStream dout = new DataOutputStream(s.getOutputStream());
				boolean isLarge = false;
				
				String request = din.readUTF();
				if (request.startsWith("[Large]")) {
					request = request.substring("[Large]".length());
					isLarge = true;
				}
				String response = handleRequest(s, request);
				if (response == null) response = "404error";
				if (response.equals("someerror")) response = "Errors 400/401/403/405/417/501/503 (go away lmao)";
				dout.writeUTF(response);
				dout.flush();
				
				if (isLarge) {
					while(true) {
						request = din.readUTF();
						if (request.startsWith("[Large]")) request = request.substring("[Large]".length());
						if (request.equals("CloseConnection")) {
							dout.writeUTF("ty :3");
							dout.flush();
							break;
						}
						response = handleRequest(s, request);
						if (response == null) response = "404error";
						if (response.equals("someerror")) response = "Errors 400/401/403/405/417/501/503 (go away lmao)";
						dout.writeUTF(response);
						dout.flush();
					}
				}
				dout.close();
				din.close();
				s.close();
			} catch (Throwable e) {
				throw new RuntimeException(e);
			}
		}
	}
}
