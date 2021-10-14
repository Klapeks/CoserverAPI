package com.klapeks.coserver.plugin.bungee;

import java.io.File;
import java.io.FileWriter;
import java.util.LinkedHashMap;

import com.klapeks.coserver.aConfig;
import com.klapeks.coserver.dFunctions;
import com.klapeks.funcs.FileCfgUtils;
import com.klapeks.funcs.dRSA;

import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

public class ConfigBungee {
	
	static final String fs = File.separator;
	
	
	private static FileWriter fw = null;
	private static Configuration config = null;
	
	static void __init() {
		try {
			File file = new File("plugins" + fs + "Coserver", "config.yml");
			if (!file.exists()) try {
				file.getParentFile().mkdirs();
				file.createNewFile();
				dFunctions.debug("§6Config was not found; Creating new one");
				fw = new FileWriter(file);
				fw.write("# Config for BungeeCord server side" + "\n");
			} catch (Throwable e) { throw new RuntimeException(e); }
			
			config = ConfigurationProvider.getProvider(YamlConfiguration.class).load(file);
			
			if (fw==null) fw = open(file);
			
			aConfig.bungee.port = g("port", getDefaultPort(), "Port");
			aConfig.useSecurity = g("useSecurity", false);
			aConfig.rsaKeySize = g("rsaKeySize", 1024, "RSA key size");
			aConfig.securityKey = g("securityKey", dRSA.generateSecretKey(10), "Security key to send to bukkit a RSA public key", "(Needs to equals with bukkit's config file)");
			aConfig.useDebugMsg = g("useDebugMsg", false, "Is debug messages will shows in console");
			
			fw.flush();
			fw.close();
			config = null; fw = null;
			if (aConfig.bungee.port<0 || aConfig.bungee.port > 65535) {
				throw new IllegalArgumentException("Port value out of range (0 <= " + aConfig.bungee.port + " <= 65535)");
			}
			if (aConfig.rsaKeySize <= 0) {
				throw new IllegalArgumentException("rsaKeySize value out of range (0 < " + aConfig.rsaKeySize + ")");
			}
		} catch (Throwable t) {
			throw new RuntimeException(t);
		}
	}
	
	private static int getBungeePort() {
		try {
			Configuration cfg = ConfigurationProvider.getProvider(YamlConfiguration.class).load(new File("config.yml"));
			@SuppressWarnings("unchecked") 
			LinkedHashMap<String, ?> m = (LinkedHashMap<String, ?>) cfg.getList("listeners").get(0);
			return Integer.parseInt((m.get("host")+"").split(":")[1]);
		} catch (Throwable e) {
			throw new RuntimeException(e);
		}
	}
	private static int getDefaultPort() {
		int port = getBungeePort();
		if (port>=3000) port-=3000;
		else if (port>=100) port-=100;
		else port=-1;
		return port;
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
