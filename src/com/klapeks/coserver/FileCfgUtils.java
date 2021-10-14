package com.klapeks.coserver;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

import org.bukkit.configuration.file.FileConfiguration;

import net.md_5.bungee.config.Configuration;

public class FileCfgUtils {

	public static FileWriter open(File file) {
		try {
			BufferedReader br = new BufferedReader(new FileReader(file));
			String e = "";
			String s; while((s=br.readLine())!=null) {
//				dFunctions.debug("§e---" + s);
				e = e + s + "\n";
			}
			br.close();
			FileWriter fw = new FileWriter(file);
//			dFunctions.debug("§a Adding to filewriter;   §b" + e);
			fw.write(e);
			return fw;
		} catch (Throwable t) {
			throw new RuntimeException(t);
		}
	}

	public static <T> T g(FileConfiguration config, FileWriter fw,  String key, T defaultValue, String... comment) {
		return g(new BukkitConfiguration(config), fw, key, defaultValue, comment);
	}
	public static <T> T g(Configuration config, FileWriter fw,  String key, T defaultValue, String... comment) {
		return g(new BungeeConfiguration(config), fw, key, defaultValue, comment);
	}
	
	@SuppressWarnings("unchecked")
	public static <T> T g(ConfigurationAdapter ca, FileWriter fw, String key, T defaultValue, String... comment) {
		try {
			if (!ca.contains(key)) {
				fw.write("\n\n");
				if (comment!=null) 
					for (String s : comment) {
						fw.write("# " + s + "\n");
					}
				if (defaultValue instanceof String) {
					defaultValue = (T) ("\"" + defaultValue + "\"");
				}
				fw.write(key + ": " + defaultValue);
				dFunctions.debug("§3Adding a §6" + defaultValue + "§3 in key §6" + key);
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
		Configuration c;
		public BungeeConfiguration(Configuration config) {
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
		FileConfiguration c;
		public BukkitConfiguration(FileConfiguration config) {
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
