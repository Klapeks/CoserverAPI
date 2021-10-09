package com.klapeks.coserver;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

@Deprecated
public class TAsync<T> {
	static List<Thread> threads = new ArrayList<>();
	static boolean closeThreads = false;
	public static void NewThread() {
		closeThreads = false;
		Thread t = new Thread() {
			public void run() {
				while (!closeThreads) {
					TAsync.process();
					try {
						sleep(1);
					} catch (InterruptedException e) {}
				}
			};
		};
		t.start();
		threads.add(t);
	}
	public static void CloseThread() {
		closeThreads = true;
	}
	
	static ArrayList<TAsync<?>> all = new ArrayList<>();
	public static void process() {
		TAsync<?> a = null;
		synchronized (all) {
			if (all.size() <= 0) return;
			a = all.remove(0);
		}
		if (a==null) return;
		Object result = a.task.get();
		a.setResult(result);
	}
	
	
	

	Supplier<T> task;
	Consumer<T> then = null;
	public TAsync(Supplier<T> task) {
		this.task = task;
		all.add(this);
	}
	
	public void then(Consumer<T> then) {
		this.then = then;
	}
	
	private T _result = null;
	@SuppressWarnings("unchecked")
	private void setResult(Object result) {
		_result = (T) result;
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
