package fr.umlv.conc.lab1;

import java.util.ArrayList;
import java.util.Objects;

public class Linked<E> {

  private static class Entry<E> {
    private final E element;
    private final Entry<E> next;

    private Entry(E element, Entry<E> next) {
      this.element = element;
      this.next = next;
    }
  }

  private Entry<E> head;

  public void addFirst(E element) {
    Objects.requireNonNull(element);
    head = new Entry<>(element, head);
  }

  public int size() {
    var size = 0;
    for (var link = head; link != null; link = link.next) {
      size++;
    }
    return size;
  }

  public static void main(String[] args) throws InterruptedException {
    var list = new Linked<String>();
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
