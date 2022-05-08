package com.klapeks.coserver;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.KeyPair;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.List;

import com.klapeks.funcs.IntPair;
import com.klapeks.funcs.NetConverter;
import com.klapeks.funcs.dRSA;

public class dCoserverServer {
	public static dCoserverServer serv;
	
	protected ServerSocket serverSocket;
	public final int port;
	private KeyPair keyPair;
	private IntPair netPair;
	boolean disabled = false;
	public dCoserverServer(int port) {
		serv = this;
		this.port = port;
		keyPair = dRSA.generateKeyPair(aConfig.rsaKeySize);
		netPair = new IntPair((int) (System.currentTimeMillis()%1000000), 144);
		new Thread() {
			@Override
			public void run() {
				try {
					serverSocket = new ServerSocket(dCoserverServer.this.port);
					onEnable();
					while (!disabled) {
						try {
							dFunctions.strong_debug("Waiting for connection");
							Socket s = serverSocket.accept();
							dFunctions.strong_debug("Some socket was connected");
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
	public String networkHandle(Socket socket, String request) {
		return networkHandle(request);
	}
	public void onLargeClose(Socket socket) {}
	
	public String handle(String request) {
		return "null";
	}
	public String securityHandle(String request) {
		return "null";
	}
	public String networkHandle(String request) {
		return "null";
	}
	public boolean handleError(Socket s, Exception e) {return false;}
	
//	private static Map<String, PublicKey> ip$password = new HashMap<>();
	private static class SocketProcessor implements Runnable {
		private Socket s;
		private dCoserverServer hs;
		public SocketProcessor(dCoserverServer hs, Socket s) {
			this.s = s;
			this.hs = hs;
		}
		private String handleRequest(Socket s, String request) {
			if (request.startsWith("enc/") && request.contains("/dec/")) {
				request = request.replaceFirst("enc/", "");
				PublicKey pk = dRSA.toPublicKey(request.split("/dec/")[0]);
				request = request.substring(request.split("/dec/")[0].length()+"/dec/".length());
				try {
					request = dRSA.rsaDecrypt(request, hs.keyPair.getPrivate());
				} catch (Throwable t) {
					if (aConfig.useDebugMsg) {
						t.printStackTrace();
						dFunctions.debug("§cErrored request -> errored response");
					}
					return "someerror";
				}
				return dRSA.rsaEncrypt(hs.securityHandle(s, request), pk);
			}
			if (request.startsWith("net/") && request.contains("/dec/")) {
				request = request.replaceFirst("net/", "");
				IntPair nk;
				try {
					nk = IntPair.from(request.split("/dec/")[0]);
					request = request.substring(request.split("/dec/")[0].length()+"/dec/".length());
					request = NetConverter.decode(request, hs.netPair.get1(), hs.netPair.get2());
				} catch (Throwable t) {
					if (aConfig.useDebugMsg) {
						t.printStackTrace();
						dFunctions.debug("§cErrored net request -> errored net response");
					}
					return "someerror";
				}
				return NetConverter.encode(hs.networkHandle(s, request), nk.get1(), nk.get2());
			}
			
			else if (request.startsWith(aConfig.securityKey+Coserver.securitySplitor)) {
				request = request.substring(request.split(Coserver.securitySplitor)[0].length()+Coserver.securitySplitor.length());
				if (request.startsWith("givemepswpls")) {
					return dRSA.fromKey(hs.keyPair.getPublic());
				}
				if (request.startsWith("givemenetpswpls")) {
					return hs.netPair.toString();
				}
			}
			try {
				return hs.handle(s, request);
			} catch (Throwable t) {
				t.printStackTrace();
				return null;
			}
		}
		static void largeResponse(DataOutputStream dout, String response) {
			final int sepr = 64000;
			String nr = "";
			List<String> list = new ArrayList<String>();
			while (response.length() > sepr) {
				nr = response.substring(0, sepr);
				response = response.substring(sepr);
				list.add(nr);
			}
			list.add(response);
			try {
				dout.writeUTF("[Large]Willbe:"+list.size());
				for (String s : list) {
					dout.writeUTF(s);
				}
				dout.flush();
			} catch (Throwable t) {
				throw new RuntimeException(t);
			}
		}
		@Override
		public void run() {
			try {
				DataInputStream din = new DataInputStream(s.getInputStream());
				DataOutputStream dout = new DataOutputStream(s.getOutputStream());
				boolean isLarge = false;
				String request = din.readUTF();
				dFunctions.strong_debug("Request was sended from " + s.getInetAddress() + ": " + request);
				if (request.startsWith("[Large]")) {
					request = request.substring("[Large]".length());
					isLarge = true;
				}
				String response = handleRequest(s, request);
				dFunctions.strong_debug("Response: " + response);
				if (response == null) response = "404error";
				if (response.equals("someerror")) response = "Errors 400/401/403/405/417/501/503 (go away lmao)";
				if (response.length() >= 64000) {
					largeResponse(dout, response);
				} else {
					dout.writeUTF(response);
					dout.flush();
				}
				
				if (isLarge) {
					while(true) {
						request = din.readUTF();
						if (request.startsWith("[Large]")) request = request.substring("[Large]".length());
						if (request.equals("CloseConnection")) {
							dout.writeUTF("ty :3");
							dout.flush();
							hs.onLargeClose(s);
							break;
						}
						response = handleRequest(s, request);
						if (response == null) response = "404error";
						if (response.equals("someerror")) response = "Errors 400/401/403/405/417/501/503 (go away lmao)";
						if (response.length() >= 64000) {
							largeResponse(dout, response);
						} else {
							dout.writeUTF(response);
							dout.flush();
						}
					}
				}
				dFunctions.strong_debug("Closing...");
				dout.close();
				din.close();
				s.close();
			} catch (Exception e) {
				if (hs.handleError(s, e)) return;
				if (aConfig.useDebugMsg) e.printStackTrace();
			}
		}
	}
}
