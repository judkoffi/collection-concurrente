package fr.umlv.conc;

import java.util.ArrayList;

/*
 * 1. Le champs counter est une data race donc l'accès concurrent a cette variable n'est pas protoger
 * 
 * 2. Même si le champs est volatile, on a encore le problème que la classe n'est pas Thread-safe
 * 
 * 3. la valeur de retour permet de s'avoir si un thread est deja passé et a modifier la value dans l'espace mémoire
 * 
 */

public class Counter {
	// private int counter;

	private volatile int counter;

	public int nextInt() {
		return counter++;
	}

	public static void main(String[] args) throws InterruptedException {
		var counter = new Counter();
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
