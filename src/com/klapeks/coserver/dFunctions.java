package com.klapeks.coserver;

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
	};
	public interface IdFunctions {
		void log(Object obj);
		default String logPrefix() {return "";}
		void scheduleAsync(Runnable r, int time);
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
	
	private static String _filter_(String string, String filter) {
		String integer = "";
		for (String s : string.split("")) {
			if (filter.contains(s)) integer=integer+s;
		}
		return integer;
	}
}
