package com.klapeks.funcs;

public class IntPair {
	int v; int t;
	public IntPair(int v, int t) {
		this.v = v;
		this.t = t;
	}
	public int get1() {
		return v;
	}
	public int get2() {
		return t;
	}
	public static IntPair from(String str) {
		str = dRSA.base64_decode(str);
		return new IntPair(Integer.parseInt(str.split("_")[0]), 
				Integer.parseInt(str.split("_")[1]));
	}
	
	@Override
	public String toString() {
		return dRSA.base64_encode(v+"_"+t);
	}
}
