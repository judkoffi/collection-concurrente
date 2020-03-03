package fr.umlv.conc;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

public class CounterAtomic {
	private final AtomicInteger counter = new AtomicInteger();

	public int nextInt() {
		for (;;) {
			var currentCounter = counter.get();
			if (counter.compareAndSet(currentCounter, currentCounter + 1)) {
				return currentCounter;
			}
		}
	}

	public static void main(String[] args) throws InterruptedException {
		var counter = new CounterAtomic();
		var threads = new ArrayList<Thread>();

		for (var i = 0; i < 4; i++) {
			var t = new Thread(() ->
				{
					for (var j = 0; j < 100_000; j++) {
						counter.nextInt();
					}
				});
			t.start();
			threads.add(t);
		}

		for (var thread : threads) {
			thread.join();
		}

		System.out.println(counter.counter);
	}
}
