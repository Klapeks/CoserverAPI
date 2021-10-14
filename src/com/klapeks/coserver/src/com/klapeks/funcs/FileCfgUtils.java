package com.klapeks.funcs;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

import com.klapeks.coserver.dFunctions;

public class FileCfgUtils {

	public static FileWriter open(File file) {
		try {
			BufferedReader br = new BufferedReader(new FileReader(file));
			String e = "";
			String s; while((s=br.readLine())!=null) {
//				dFunctions.debug("�e---" + s);
				e = e + s + "\n";
			}
			br.close();
			FileWriter fw = new FileWriter(file);
//			dFunctions.debug("�a Adding to filewriter;   �b" + e);
			fw.write(e);
			return fw;
		} catch (Throwable t) {
			throw new RuntimeException(t);
		}
	}

	public static <T> T g(org.bukkit.configuration.file.FileConfiguration config, FileWriter fw,  String key, T defaultValue, String... comment) {
		return ga(new BukkitConfiguration(config), fw, key, defaultValue, comment);
	}
	public static <T> T g(net.md_5.bungee.config.Configuration config, FileWriter fw,  String key, T defaultValue, String... comment) {
		return ga(new BungeeConfiguration(config), fw, key, defaultValue, comment);
	}
	public static <T> T gb(org.bukkit.configuration.file.FileConfiguration config, FileWriter fw,  String key, T defaultValue, String... comment) {
		return ga(new BukkitConfiguration(config), fw, key, defaultValue, comment);
	}
	public static <T> T gbg(net.md_5.bungee.config.Configuration config, FileWriter fw,  String key, T defaultValue, String... comment) {
		return ga(new BungeeConfiguration(config), fw, key, defaultValue, comment);
	}
	
	@SuppressWarnings("unchecked")
	public static <T> T ga(ConfigurationAdapter ca, FileWriter fw, String key, T defaultValue, String... comment) {
		try {
			if (!ca.contains(key)) {
				fw.write("\n\n");
				if (comment!=null)
					for (String s : comment) {
						for (String _s : s.split("\n")) {
							fw.write("# " + _s + "\n");
						}
					}
				if (defaultValue instanceof String) {
					defaultValue = (T) ("\"" + defaultValue + "\"");
				}
				fw.write(key + ": " + defaultValue);
				dFunctions.debug("�3Adding a �6" + defaultValue + "�3 in key �6" + key);
				return defaultValue;
			} else {
				Object o = ca.get(key);
				try {
					return (T) o;
				} catch (Throwable t) {
					return defaultValue;
				}
			}
		} catch (Throwable e) {
			throw new RuntimeException(e);
		}
	}
	
	static interface ConfigurationAdapter {
		public boolean contains(String key);
		public Object get(String key);
	}

	static class BungeeConfiguration implements ConfigurationAdapter {
		net.md_5.bungee.config.Configuration c;
		public BungeeConfiguration(net.md_5.bungee.config.Configuration config) {
			c = config;
		}
		public boolean contains(String key) {
			return c.contains(key);
		}
		public Object get(String key) {
			return c.get(key);
		}
	}
	static class BukkitConfiguration implements ConfigurationAdapter {
		org.bukkit.configuration.file.FileConfiguration c;
		public BukkitConfiguration(org.bukkit.configuration.file.FileConfiguration config) {
			c = config;
		}
		public boolean contains(String key) {
			return c.contains(key);
		}
		public Object get(String key) {
			return c.get(key);
		}
	}
}
