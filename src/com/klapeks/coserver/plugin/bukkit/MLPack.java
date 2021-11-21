package com.klapeks.coserver.plugin.bukkit;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

import com.klapeks.coserver.IMLPack;
import com.klapeks.coserver.dFunctions;

public class MLPack implements IMLPack<JavaPlugin>{
	
	public void init(JavaPlugin plugin) {
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
				@SuppressWarnings("deprecation")
				@Override
				public void scheduleAsync(Runnable r, int time) {
					Bukkit.getScheduler().scheduleAsyncDelayedTask(plugin, r, time);
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
				@SuppressWarnings("deprecation")
				@Override
				public void scheduleAsync(Runnable r, int time) {
					Bukkit.getScheduler().scheduleAsyncDelayedTask(plugin, r, time);
				}
				@Override
				public void shutdown() {
					Bukkit.shutdown();
				}
			});
		}
	}

	public void load(JavaPlugin plugin) {
		dFunctions.log("§3CoserverAPI is loading");
		ConfigBukkit.__init();
	}
	
	public void enable(JavaPlugin plugin) {
		dFunctions.log("§aCoserverAPI is enabling");
	}

	public void disable(JavaPlugin plugin) {
		dFunctions.log("§cCoserverAPI is disabling");
	}
	
}
