package com.klapeks.funcs;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class Async<T> {

	Consumer<T> then = null;
	public Async(Supplier<T> task) {
		new Thread(() -> {
			setResult(task.get());
		}).start();
	}
	
	public void then(Consumer<T> then) {
		if (resulted) {
			then.accept(_result);
			return;
		}
		this.then = then;
	}

	boolean resulted;
	private T _result = null;
	private void setResult(T result) {
		resulted = true;
		_result = result;
		if (then != null) {
			then.accept(_result);
		}
	}
	
	public T await() {
		while(!resulted) {
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {}
		}
		return _result;
	}
}
