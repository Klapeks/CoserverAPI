package com.klapeks.coserver.plugin.bungee;

import net.md_5.bungee.api.plugin.Plugin;

public class Main extends Plugin {
	
	static MLPack bungee = new MLPack();

	public Main() {
		bungee.init(this);
	}
	
	@Override
	public void onLoad() {
		bungee.load(this);
	}
	
	
	@Override
	public void onEnable() {
		bungee.enable(this);
	}
	
	@Override
	public void onDisable() {
		bungee.disable(this);
	}
}
