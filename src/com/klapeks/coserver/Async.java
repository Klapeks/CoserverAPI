package com.klapeks.coserver;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class Async<T> {

	Supplier<T> task;
	Consumer<T> then = null;
	public Async(Supplier<T> task) {
		this.task = task;
		new Thread(() -> {
			T r = task.get();
			setResult(r);
		}).start();
	}
	
	public void then(Consumer<T> then) {
		this.then = then;
	}
	
	private T _result = null;
	private void setResult(T result) {
		_result = result;
		if (then != null) {
			then.accept(_result);
		}
	}
	
	public T await() {
		while(_result==null) {
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {}
		}
		return _result;
	}
}
