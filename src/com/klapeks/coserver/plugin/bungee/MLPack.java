package com.klapeks.coserver.plugin.bungee;

import com.klapeks.coserver.IMLPack;
import com.klapeks.coserver.aConfig;
import com.klapeks.coserver.dFunctions;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Plugin;

public class MLPack implements IMLPack<Plugin> {
	
	public void init(Plugin bungee) {
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
			@Override
			public void shutdown() {
				ProxyServer.getInstance().stop();
			}
		});
	}

	public void load(Plugin bungee) {
		dFunctions.log("§3CoserverAPI is loading");
		ConfigBungee.__init();
		SuperCoServer.BungeeCoserv = new SuperCoServer(aConfig.bungee.port);
		dFunctions.log("§aBungee was loaded.");
	}
	
	public void enable(Plugin bungee) {
		dFunctions.log("§aCoserverAPI is enabling");
	}

	public void disable(Plugin bungee) {
		dFunctions.log("§cCoserverAPI is disabling");
		SuperCoServer.BungeeCoserv.shutdown();
	}
	
}
