package fr.umlv.conc;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

public class Counter2 {
  /*
   * 4. getAndIncrement permet d'effectuer une incrementation de manière atomiques
   * 
   * 5. lock-free veut dire sans block utilisant un object lock
   */
  private final AtomicInteger counter = new AtomicInteger();

  public int nextInt() {
    return counter.getAndIncrement();
  }

  public static void main(String[] args) throws InterruptedException {
    var counter = new Counter2();
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
