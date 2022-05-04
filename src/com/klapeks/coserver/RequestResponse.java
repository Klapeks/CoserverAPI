package com.klapeks.coserver;

import com.klapeks.funcs.Nullable;

@FunctionalInterface
public interface RequestResponse {
	@Nullable
	public String handle(String[] args);
}
