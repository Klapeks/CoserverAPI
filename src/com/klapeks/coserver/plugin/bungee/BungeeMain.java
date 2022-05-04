package com.klapeks.coserver.plugin.bungee;

import java.util.concurrent.TimeUnit;

import com.klapeks.coserver.Coserver;
import com.klapeks.coserver.aConfig;
import com.klapeks.coserver.dFunctions;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Plugin;

public class BungeeMain extends Plugin {

	public BungeeMain() {
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
			public void scheduleAsync(Runnable r) {
				ProxyServer.getInstance().getScheduler().runAsync(BungeeMain.this, r);
			}
			@Override
			public void shutdown() {
				ProxyServer.getInstance().stop();
			}
		});
	}
	
	@Override
	public void onLoad() {
		dFunctions.log("§3CoserverAPI is loading");
		ConfigCord.__init();
		SuperCoServer.BungeeCoserv = new SuperCoServer(aConfig.bungee.port);
		dFunctions.log("§aBungee was loaded.");
	}
	
	
	@Override
	public void onEnable() {
		dFunctions.log("§aCoserverAPI is enabling");
		if (aConfig.bungee.strongDebug) {
			if (aConfig.useSecurity) {
				SuperCoServer.BungeeCoserv.addSecurityHandler("coservertestbtchecker", (msg) -> "all is good");
			}
			else SuperCoServer.BungeeCoserv.addHandler("coservertestbtchecker", (msg) -> "all is good");
		}
		getProxy().getScheduler().schedule(this, Coserver::every5MinutesCheck, 5, TimeUnit.MINUTES);
	}
	
	@Override
	public void onDisable() {
		dFunctions.log("§cCoserverAPI is disabling");
		Coserver.closeAll();
		SuperCoServer.BungeeCoserv.shutdown();
	}
}
