package com.klapeks.coserver.plugin.bungee;

import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;

import com.google.inject.Inject;
import com.klapeks.coserver.Coserver;
import com.klapeks.coserver.aConfig;
import com.klapeks.coserver.dFunctions;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.proxy.ProxyServer;

@Plugin(id="coserverapi")
public class VelocityMain {
	
	ProxyServer server;
	@Inject
    public VelocityMain(ProxyServer server, Logger logger) {
		this.server = server;
		dFunctions.setFunctions(new dFunctions.IdFunctions() {
			@Override
			public void log(Object obj) {
				logger.info(obj+"");
			}
			@Override
			public String logPrefix() {
				return "§9[§cCoserv§9]§r ";
			}
			@Override
			public void scheduleAsync(Runnable r) {
				new Thread(r).start();
			}
			@Override
			public void shutdown() {
				server.shutdown();
			}
		});
		ConfigCord.__init();
		SuperCoServer.BungeeCoserv = new SuperCoServer(aConfig.bungee.port);
		dFunctions.log("§aVelocity was loaded.");
    }
	@Subscribe
	public void onProxyInitialization(ProxyInitializeEvent event) {
		dFunctions.log("§aCoserverAPI is enabling");
		if (aConfig.bungee.strongDebug) {
			if (aConfig.useSecurity) {
				SuperCoServer.BungeeCoserv.addSecurityHandler("coservertestbtchecker", (msg) -> "all is good");
			}
			else SuperCoServer.BungeeCoserv.addHandler("coservertestbtchecker", (msg) -> "all is good");
		}
		server.getScheduler().buildTask(this, Coserver::every5MinutesCheck).repeat(5, TimeUnit.MINUTES).schedule();
	}
	@Subscribe
	public void onProxyShutdown(ProxyShutdownEvent event) {
		dFunctions.log("§cCoserverAPI is disabling");
		Coserver.closeAll();
		SuperCoServer.BungeeCoserv.shutdown();
	}
}
