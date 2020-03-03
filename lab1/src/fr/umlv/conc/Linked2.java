package fr.umlv.conc;

import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

/*
 * 3. Le faite de stocker l'element E dans un AtomicReference fait payer le cout d'une indirection pour aller chercher la valeur de E
 */

public class Linked2<E> {

	private static class Entry<E> {
		private final E element;
		private final Entry<E> next;

		private Entry(E element, Entry<E> next) {
			this.element = element;
			this.next = next;
		}
	}

	private final AtomicReference<Entry<E>> head = new AtomicReference<>();

	public void addFirst(E element) {
		Objects.requireNonNull(element);

		for (;;) {
			var currentHead = head.get();
			var newHead = new Entry<>(element, currentHead);
			if (head.compareAndSet(currentHead, newHead)) {
				return;
			}
		}
	}

	public int size() {
		var size = 0;
		// link est une variable local donc elle n'est pas forcement relu en
		// mémoire
		for (var link = head.get(); link != null; link = link.next) {
			size++;
		}
		return size;
	}

	public static void main(String[] args) throws InterruptedException {
		var list = new Linked2<String>();
		var threads = new ArrayList<Thread>();

		for (var i = 0; i < 4; i++) {
			var t = new Thread(() ->
				{
					for (var j = 0; j < 100_000; j++) {
						list.addFirst("hello " + j);
					}
				});
			t.start();
			threads.add(t);
		}

		for (var thread : threads) {
			thread.join();
		}

		System.out.println(list.size());

	}
}
