package com.klapeks.coserver;

public interface IMLPack<T> {
	void init(T plugin);
	void load(T plugin);
	void enable(T plugin);
	void disable(T plugin);
}
