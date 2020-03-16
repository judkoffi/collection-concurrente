package fr.upem.conc.lab2;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.stream.IntStream;

public class COWSet<E> {

  private static final Object[] EMPTY = new Object[0];
  private final E[][] hashArray;

  private static final VarHandle ARRAY_HANDLE =
      MethodHandles.arrayElementVarHandle(Object[][].class);


  @SuppressWarnings("unchecked")
  public COWSet(int capacity) {
    var array = new Object[capacity][];
    Arrays.setAll(array, __ -> EMPTY);
    this.hashArray = (E[][]) array;
  }

  public boolean add(E element) {
    Objects.requireNonNull(element);
    var index = element.hashCode() % hashArray.length;

    for (;;) {
      var oldArray = (E[]) ARRAY_HANDLE.getVolatile(hashArray, index);
      for (var e : hashArray[index]) {
        if (element.equals(e)) {
          return false;
        }
      }
      var newArray = Arrays.copyOf(oldArray, oldArray.length + 1);
      newArray[oldArray.length] = element;
      hashArray[index] = newArray;

      if (ARRAY_HANDLE.compareAndSet(hashArray, index, oldArray, newArray))
        return true;
    }
  }

  public void forEach(Consumer<? super E> consumer) {
    for (var index = 0; index < hashArray.length; index++) {
      var oldArray = (E[]) ARRAY_HANDLE.getVolatile(hashArray, index);
      for (var element : oldArray) {
        consumer.accept(element);
      }
    }
  }

  public static void main(String[] args) throws InterruptedException {
    var threadCount = 2;
    var set = new COWSet<Integer>(20);
    var threads = new ArrayList<Thread>();

    for (var i = 0; i < threadCount; i++) {
      Thread t = new Thread(() -> IntStream.range(0, 200_000).forEach(set::add));
      t.start();
      threads.add(t);
    }

    for (var t : threads) {
      t.join();
    }

    set.forEach(System.out::println);
  }
}
