package com.klapeks.coserver;

import java.util.function.Function;
import com.klapeks.funcs.uArrayMap;

public class Addoner {
	
	static uArrayMap<String, Function<Object[], Object[]>> all = new uArrayMap<>();
	
	public static void on(String message, Function<Object[], Object[]> func) {
		all.addIn(message, func);
	}
	
	public static Object[] run(String message, Object... args) {
		if (!all.containsKey(message)) return null;
		Object[] o = null;
		for (Function<Object[], Object[]> func : all.get(message)) {
			o = func.apply(args);
			if (o!=null) return o;
		}
		return null;
	}
	
}
