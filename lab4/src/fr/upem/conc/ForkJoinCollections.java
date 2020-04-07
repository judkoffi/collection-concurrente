package fr.upem.conc;

import java.util.Arrays;
import java.util.Collection;
import java.util.Random;
import java.util.Spliterator;
import java.util.function.BinaryOperator;

public class ForkJoinCollections {

  public static <V, T> V forkJoinReduce(Collection<T> collection, int threshold, V initialValue,
      BinaryOperator<V> accumulator, BinaryOperator<T> combiner) {
    return forkJoinReduce(collection.spliterator(), threshold, initialValue, accumulator, combiner);
  }

  private static <V, T> V forkJoinReduce(Spliterator<T> spliterator, int threshold, V initialValue,
      BinaryOperator<V> accumulator, BinaryOperator<T> combiner) {
    
    


    return (V) new Object();
  }


  public static <T> T sequentialReduce(Spliterator<T> spliterator, T initial,
      BinaryOperator<T> accumulator) {

    /*
     * Classe local car si on utilise une variable local dans une lambda on peut pas hchager sa
     * valeur
     */

    /*
     * Ici, on a besoin de faire un effet de bord, or cela n'est pas possible avec une variable si
     * on utilise une lambda
     */
    class Box {
      // champs mutable
      private T acc = initial;
    }

    var box = new Box();
    while (spliterator.tryAdvance(e ->
    {
      box.acc = accumulator.apply(box.acc, e);
    }));
    return box.acc;
  }

  public static void main(String[] args) {
    var random = new Random(10);
    var array = random.ints(1_000_000, 0, 1000).toArray();


    System.out.println(sequentialReduce(Arrays.spliterator(array), 0, (acc, value) -> acc + value));


    // sequential
    System.out.println(Arrays.stream(array).sum());

    // fork/join
    // var list = IntStream.range(0, 10_000).boxed().collect(Collectors.toList());
    // System
    // .out
    // .println(forkJoinReduce(list, 1_000, 0, (acc, value) -> acc + value, (acc1, acc2) -> acc1 +
    // acc2));
  }
}

