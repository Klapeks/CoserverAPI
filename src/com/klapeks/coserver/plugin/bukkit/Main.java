package com.klapeks.coserver.plugin.bukkit;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

import com.klapeks.coserver.dFunctions;

public class Main extends JavaPlugin {
	
	static JavaPlugin bukkit;
	
	public Main() {
		bukkit = this;
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
					Bukkit.getScheduler().scheduleAsyncDelayedTask(bukkit, r, time);
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
					Bukkit.getScheduler().scheduleAsyncDelayedTask(bukkit, r, time);
				}
			});
		}
	}
	
	@Override
	public void onLoad() {
		ConfigBukkit.__init();
	}
	
}
