package com.klapeks.coserver;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.Bukkit;

import java.util.Random;

public class dFunctions {

	
	private static boolean isAlreadySetted = false;
	public static void setFunctions(IdFunctions func) throws RuntimeException {
		if (isAlreadySetted) throw new RuntimeException("A function has been already set");
		isAlreadySetted = true;
		t = func;
	}
	private static IdFunctions t = new IdFunctions() {
		@Override
		public void log(Object obj) {
			System.out.println(obj.toString());
		}

		@Override
		public void scheduleAsync(Runnable r, int time) {
			r.run();
		}
		@Override
		public void shutdown() {
			System.err.println("Shutdown methot is not found :(");
		}
	};
	public interface IdFunctions {
		void log(Object obj);
		default String logPrefix() {return "";}
		void scheduleAsync(Runnable r, int time);
		void shutdown();
	}
	
	

	public static void shutdown() {
		t.shutdown();
	}

	public static void log(Object obj) {
		log_(t.logPrefix() + obj);
	}
	public static void debug(Object obj) {
		if (aConfig.useDebugMsg) log_("§6{DEBUG}§r: " + obj);
	}
	public static void log_(Object obj) {
		t.log(obj);
	}
	public static void scheduleAsync(Runnable r, int time) {
		t.scheduleAsync(r, time);
	}
	

	public static int getRandom(int from, int to) {
		return getRandom(new Random(), from, to);
	}
	public static int getRandom(Random random, int from, int to) {
        return random.nextInt((to - from) + 1) + from;
	}
	
	
	
	public static int toInt(Object obj) {
		if (obj==null) return 0;
		if (obj instanceof Integer) {
			return (Integer) obj;
		}
		String ss = obj+"";
		try {return Integer.parseInt(ss);} catch(Throwable e) {};
		ss = _filter_(ss, "-1234567890");
		if (ss.equals("")) return 0;
		try {return Integer.parseInt(ss);} catch(Throwable e) {return 0;}
	}
	public static long toLong(Object obj) {
		if (obj==null) return 0;
		if (obj instanceof Long) {
			return (Long) obj;
		}
		String ss = obj+"";
		try {return Long.parseLong(ss);} catch(Throwable e) {};
		ss = _filter_(ss, "-1234567890");
		if (ss.equals("")) return 0;
		try {return Long.parseLong(ss);} catch(Throwable e) {return 0;}
	}
	
	private static String _filter_(String string, String filter) {
		String integer = "";
		for (String s : string.split("")) {
			if (filter.contains(s)) integer=integer+s;
		}
		return integer;
	}
	
	
	
	/**Convert <b>"key:value$$k2:v2$$k3:val3"</b> with sep <b>"$$"</b> to <b>HashMap</b>*/
	public static HashMap<String, String> string_HashMap(String str, String separator) {
		HashMap<String, String> hm = new HashMap<>();
		for (String s : str.split(separator)) {
			String key = s.split(":")[0];
			s = s.replaceFirst(key+":", "");
			if (s.startsWith(" ")) s = s.replaceFirst(" ", "");
			hm.put(key, s);
		}
		return hm;
	}
	
	/**Convert <b>HashMap</b> with sep <b>"$$"</b> to <b>"key:value$$k2:v2$$k3:val3"</b>*/
	public static String hashMap_String(HashMap<String, String> hm, String separator) {
		String s = "";
		for (String key : hm.keySet()) {
			s = s + separator + key + ":" + hm.get(key);
		}
		if (s.startsWith(separator)) s = s.replaceFirst(separator, "");
		return s;
	}

	public static <K extends Comparable<? super K>, V> Map<K, V> sortByKey(Map<K, V> map) {
		if (map==null) return null;
        List<Entry<K, V>> list = new ArrayList<>(map.entrySet());
        list.sort(Entry.comparingByKey());

        Map<K, V> result = new LinkedHashMap<>();
        for (Entry<K, V> entry : list) {
            result.put(entry.getKey(), entry.getValue());
        }

        return result;
	}
	public static <K, V extends Comparable<? super V>> Map<K, V> sortByValue(Map<K, V> map) {
		if (map==null) return null;
        List<Entry<K, V>> list = new ArrayList<>(map.entrySet());
        list.sort(Entry.comparingByValue());

        Map<K, V> result = new LinkedHashMap<>();
        for (Entry<K, V> entry : list) {
            result.put(entry.getKey(), entry.getValue());
        }
        return result;
	}

	public static List<String> massiveToArray(String[] strings) {
		ArrayList<String> list = new ArrayList<>();
		for (String s : strings) {
			list.add(s);
		}
		return list;
	}
}
