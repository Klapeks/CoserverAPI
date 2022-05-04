package com.klapeks.funcs;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.klapeks.funcs.FileCfgUtils.ConfigurationAdapter;

public class EasyYaml implements ConfigurationAdapter {

	private Map<String, Object> data = new LinkedHashMap<>();
	public static void main(String[] args) throws Exception {
		EasyYaml yaml = parse(new File("H:\\EclipseSourceCode\\PluginDeveloper\\CoLinker2\\config.bukkit.yml"));
		yaml.print();
	}
	
	
	private void print() {
		data.forEach((s,a) -> {
			System.out.println(s + ": " + a);
		});		
	}
	@SuppressWarnings("unchecked")
	public List<Object> getList(String key) {
		return (List<Object>) data.get(key);
	}
	public int getInteger(String key) {
		return (int) data.get(key);
	}
	public long getLong(String key) {
		return (long) data.get(key);
	}
	public boolean getBoolean(String key) {
		return (boolean) data.get(key);
	}
	public double getDouble(String key) {
		return (double) data.get(key);
	}
	public float getFloat(String key) {
		return (float) data.get(key);
	}
	public String getString(String key) {
		return data.get(key)+"";
	}
	public Object get(String key) {
		return data.get(key);
	}
	public boolean contains(String key) {
		return data.containsKey(key);
	}
	
	private void put(String key, String val) {
		data.put(key, parseObject(val));
	}
	private static Object parseObject(String str) {
		try {
			return Integer.parseInt(str);
		} catch (Throwable t) {}
		try {
			return Long.parseLong(str);
		} catch (Throwable t) {}
		try {
			return Float.parseFloat(str);
		} catch (Throwable t) {}
		try {
			return Double.parseDouble(str);
		} catch (Throwable t) {}
		if (str.equals("true")) return true;
		if (str.equals("false")) return false;
		if (str.startsWith("\"") && str.endsWith("\"")) {
			return str.substring(1, str.length()-1);
		}
		if (str.startsWith("'") && str.endsWith("'")) {
			return str = str.substring(1, str.length()-1);
		}
		if (str.startsWith("[") && str.endsWith("]")) {
			List<Object> list = new ArrayList<>();
			str = str.substring(1, str.length()-1);
			String[] cc = str.split("");
			str = "";
			String isStr = null;
			for (int i = 0; i < cc.length; i++) {
				if (str.isEmpty()) {
					if (cc[i].equals(" ")) continue;
					if (cc[i].equals("\"") || cc[i].equals("'")) {
						isStr = cc[i];
						continue;
					}
				}
				if (isStr!=null) {
					if (cc[i].equals("\\")) {
						i++;
					}
					if (cc[i].equals(isStr)) {
						isStr = null;
						continue;
					}
				} else if (cc[i].equals(",")) {
					list.add(parseObject(str));
					str = "";
					continue;
				}
				str += cc[i];
			}
			if (!str.isEmpty()) list.add(parseObject(str));
			return list;
		}
		return str;
	}


	public static EasyYaml parse(File file) {
		try {
			EasyYaml yaml = new EasyYaml();
			BufferedReader reader = new BufferedReader(new FileReader(file));
			try {
				List<String> array = new ArrayList<>();
//				Map<String, String> dat 
				List<String> insections = new ArrayList<>();
				String prev = null;
				while (true) {
					String line = reader.readLine();
					if (line==null) break;
					if (line.isEmpty()) continue;
					if (line.startsWith("#")) continue;
					while (line.endsWith(" ")) line = line.substring(0, line.length()-1);
					line = line.replace("\t", "    ");
					{
						int tabs = 0;
						while (line.startsWith("  ")) {
							line = line.substring(2);
							tabs++;
						}
						if (line.isEmpty()) continue;
						if (line.startsWith(" ")) throw new Exception("Parsing error");
						if (line.startsWith("#")) continue;
						if (line.startsWith("-")) {
							line = line.substring(1);
							while(line.startsWith(" ")) line = line.substring(1);
							array.add(line);
							continue;
						}
						if (!array.isEmpty()) {
							String s = prev + " " + array.toString();
							array.clear();
							for (int i = insections.size()-1; i>=0;i--) {
								s = insections.get(i) + "." + s;
							}
							String key = s.split(":")[0];
							s = s.substring(key.length()+1);
							while(s.startsWith(" ")) s = s.substring(1);
							yaml.put(key, s);
						}
						while (insections.size() > tabs) {
							insections.remove(insections.size()-1);
						}
						if (insections.size() < tabs) {
							if (!prev.endsWith(":")) throw new Exception("Parsing error");
							insections.add(prev.substring(0, prev.length()-1));
						}
						for (int i = insections.size()-1; i>=0;i--) {
							line = insections.get(i) + "." + line;
						}
					}
					if (line.endsWith(":")) {
						String[] ss = line.split("\\.");
						prev = ss[ss.length-1];
					} else {
						String key = line.split(":")[0];
						line = line.substring(key.length()+1);
						while(line.startsWith(" ")) line = line.substring(1);
						yaml.put(key, line);
					}
				}
				if (!array.isEmpty()) {
					String s = prev + " " + array.toString();
					array.clear();
					for (int i = insections.size()-2; i>=0;i--) {
						s = insections.get(i) + "." + s;
					}
					String key = s.split(":")[0];
					s = s.substring(key.length()+1);
					while(s.startsWith(" ")) s = s.substring(1);
					yaml.put(key, s);
				}
				return yaml;
			} finally {
				reader.close();
			}
		} catch (Throwable t) {
			throw new RuntimeException("YAML Parser exception", t);
		}
		
	}

	
}
