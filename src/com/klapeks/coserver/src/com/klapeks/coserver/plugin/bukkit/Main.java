package com.klapeks.coserver.plugin.bukkit;

import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {
	
	static MLPack plugin = new MLPack();
	
	public Main() {
		plugin.init(this);
	}
	
	@Override
	public void onLoad() {
		plugin.load(this);
	}
	
	@Override
	public void onEnable() {
		plugin.enable(this);
	}
	
	@Override
	public void onDisable() {
		plugin.disable(this);
	}
}
