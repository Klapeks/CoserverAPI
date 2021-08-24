package com.klapeks.coserver.plugin.bungee;

import com.klapeks.coserver.dFunctions;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Plugin;

public class Main extends Plugin {

	static Plugin bungee;
	
	public Main() {
		bungee = this;
		dFunctions.setFunctions(new dFunctions.IdFunctions() {
			@Override
			public void log(Object obj) {
				ProxyServer.getInstance().getLogger().info(obj+"");
			}
			@Override
			public String logPrefix() {
				return "§9[§cCoserv§9]§r ";
			}
			@Override
			public void scheduleAsync(Runnable r, int time) {
				ProxyServer.getInstance().getScheduler().runAsync(bungee, r);
			}
		});
	}
	
	@Override
	public void onLoad() {
		ConfigBungee.__init();
		dFunctions.log("§aBungee was loaded.");
	}
	
	
	@Override
	public void onEnable() {
		BungeeCoserv.__init__();
	}
	
	@Override
	public void onDisable() {
		BungeeCoserv.server.shutdown();
	}
}
