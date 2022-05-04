package com.klapeks.coserver.plugin.bukkit;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

import com.klapeks.coserver.Coserver;
import com.klapeks.coserver.dFunctions;

public class Main extends JavaPlugin {
	
	public Main() {
		if (!Bukkit.getVersion().contains("1.8")) {
			dFunctions.setFunctions(new dFunctions.IdFunctions() {
				@Override
				public void log(Object obj) {
					Bukkit.getLogger().info(obj+"");
				}
				@Override
				public String logPrefix() {
					return "§9[§cCoserv§9]§r ";
				}
				@Override
				public void scheduleAsync(Runnable r) {
					Bukkit.getScheduler().runTaskAsynchronously(Main.this, r);
				}
				@Override
				public void shutdown() {
					Bukkit.shutdown();
				}
			});
		} else {
			dFunctions.setFunctions(new dFunctions.IdFunctions() {
				@Override
				public void log(Object obj) {
					Bukkit.getLogger().info(ChatColor.stripColor(obj+""));
				}
				@Override
				public String logPrefix() {
					return "[Coserv] ";
				}
				@Override
				public void scheduleAsync(Runnable r) {
					Bukkit.getScheduler().runTaskAsynchronously(Main.this, r);
				}
				@Override
				public void shutdown() {
					Bukkit.shutdown();
				}
			});
		}
	}
	
	@Override
	public void onLoad() {
		dFunctions.log("§3CoserverAPI is loading");
		ConfigBukkit.__init();
	}
	
	@Override
	public void onEnable() {
		dFunctions.log("§aCoserverAPI is enabling");
		Bukkit.getScheduler().scheduleSyncRepeatingTask(this, Coserver::every5MinutesCheck, 20*60*5, 20*60*5);
	}
	
	@Override
	public void onDisable() {
		dFunctions.log("§cCoserverAPI is disabling");
		Coserver.closeAll();
	}
}
