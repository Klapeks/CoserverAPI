package com.klapeks.coserver.plugin.bukkit;

import java.io.File;
import java.io.FileWriter;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import com.klapeks.coserver.aConfig;
import com.klapeks.coserver.dFunctions;
import com.klapeks.funcs.FileCfgUtils;
import com.klapeks.funcs.dRSA;

public class ConfigBukkit {
	
	static final String fs = File.separator;
	
	
	private static FileWriter fw = null;
	private static FileConfiguration config = null;
	
	static void __init() {
		try {
			File file = new File("plugins" + fs + "Coserver" + fs + "config.yml");
			if (!file.exists()) try { 
				file.getParentFile().mkdirs(); 
				file.createNewFile();
				dFunctions.debug("§6Config was not found; Creating new one");
				fw = new FileWriter(file);
				fw.write("# Config for Bukkit server side" + "\n");
			} catch (Throwable e) { throw new RuntimeException(e); }
			config = YamlConfiguration.loadConfiguration(file);
			
			if (fw==null) fw = open(file);
			
			aConfig.bukkit.ip = g("bungee.ip", "0.0.0.0");
			aConfig.bukkit.port = g("bungee.port", 25565-3000);
			aConfig.useSecurity = g("useSecurity", true);
			aConfig.rsaKeySize = g("rsaKeySize", 1024, "RSA key size");
			aConfig.securityKey = g("securityKey", dRSA.generateSecretKey(10), "Security key to get a RSA public key from bungeecord", "(Needs to equals with bungeecord's config file)");
			aConfig.useDebugMsg = g("useDebugMsg", false, "Is debug messages will shows in console");
			
			fw.flush();
			fw.close();
			config = null; fw = null;
			if (aConfig.bukkit.port<0 || aConfig.bukkit.port > 65535) {
				throw new IllegalArgumentException("Port value out of range (0 <= " + aConfig.bukkit.port + " <= 65535)");
			}
			if (aConfig.rsaKeySize <= 0) {
				throw new IllegalArgumentException("rsaKeySize value out of range (0 < " + aConfig.rsaKeySize + ")");
			}
		} catch (Throwable t) {
			throw new RuntimeException(t);
		}
	}
	private static FileWriter open(File file) {
		return FileCfgUtils.open(file);
	}
	private static <T> T g(String key, T defaultValue, String... comment) {
		return FileCfgUtils.g(config, fw, key, defaultValue, comment);
	}
//	private static void copyConfig(File to, Function<String, String> placeholders) {
//		try {
//			if (to.exists()) to.delete();
//			else to.getParentFile().mkdirs();
//			to.createNewFile();
//			
//			BufferedReader br = new BufferedReader(new InputStreamReader(MainBungee.bungee.getResourceAsStream("config.yml")));
//			String line = null;
//			FileWriter fw = new FileWriter(to);
//			while ((line=br.readLine())!=null) {
//				fw.append(placeholders.apply(line)+"\r\n");
//			}
//			br.close();
//			fw.flush();
//			fw.close();
//
//		} catch (Throwable e) {
//			throw new RuntimeException(e);
//		}
//	}

}
