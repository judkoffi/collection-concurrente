package fr.upem.conc.lab2;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class LockFreeStringList {
  static final class Entry {
    private final String element;
    private volatile Entry next;

    Entry(String element) {
      this.element = element;
    }
  }

  private final Entry head;

  private static final VarHandle NEXT_HANDLE;

  static {
    var lookup = MethodHandles.lookup();
    try {
      NEXT_HANDLE = lookup.findVarHandle(Entry.class, "next", Entry.class);
    } catch (NoSuchFieldException | IllegalAccessException e) {
      throw new AssertionError(e);
    }
  }

  public LockFreeStringList() {
    head = new Entry(null); // fake first entry
  }

  public void addLast(String element) {
    Objects.requireNonNull(element);

    var entry = new Entry(element);
    var last = head;

    for (;;) {
      var next = last.next;
      if (next == null) {
        if (NEXT_HANDLE.compareAndSet(last, null, entry)) {
          return;
        }
        next = last.next;
      }
      last = next;
    }
  }

  public int size() {
    var count = 0;
    for (var e = head.next; e != null; e = e.next) {
      count++;
    }
    return count;
  }

  private static Runnable createRunnable(LockFreeStringList list, int id) {
    return () ->
    {
      for (var j = 0; j < 10_000; j++) {
        list.addLast(id + " " + j);
      }
    };
  }

  public static void main(String[] args) throws InterruptedException, ExecutionException {
    var threadCount = 5;
    var list = new LockFreeStringList();

    var tasks = IntStream
      .range(0, threadCount)
      .mapToObj(id -> createRunnable(list, id))
      .map(Executors::callable)
      .collect(Collectors.toList());

    var executor = Executors.newFixedThreadPool(threadCount);
    var futures = executor.invokeAll(tasks);
    executor.shutdown();

    for (var future : futures) {
      future.get();
    }

    System.out.println(list.size());
  }
}
