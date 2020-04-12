package fr.upem.conc;

import java.util.Collection;
import java.util.Spliterator;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;

public class ForkJoinCollections {
  private ForkJoinCollections() {}

  @SuppressWarnings("serial")
  public static class ReducerTask<V, T> extends RecursiveTask<V> {
    private final V initialValue;
    private final BiFunction<T, V, V> accumulator;
    private final BinaryOperator<V> combiner;
    private final Spliterator<T> spliterator;
    private final int threshold;

    private ReducerTask(Spliterator<T> spliterator, V initial, int threshold,
        BiFunction<T, V, V> accumulator, BinaryOperator<V> combiner) {
      this.spliterator = spliterator;
      this.initialValue = initial;
      this.threshold = threshold;
      this.combiner = combiner;
      this.accumulator = accumulator;
    }

    @Override
    protected V compute() {
      var size = spliterator.estimateSize();

      if (size <= threshold)
        return sequentialReduce(spliterator, initialValue, accumulator);

      var split = spliterator.trySplit();
      var part1 = new ReducerTask<>(spliterator, initialValue, threshold, accumulator, combiner);
      var part2 = new ReducerTask<>(split, initialValue, threshold, accumulator, combiner);
      part1.fork();
      var result2 = part2.compute();
      var result1 = part1.join();
      return combiner.apply(result1, result2);
    }
  }

  public static <V, T> V forkJoinReduce(Collection<T> collection, int threshold, V initialValue,
      BiFunction<T, V, V> accumulator, BinaryOperator<V> combiner) {
    return forkJoinReduce(collection.spliterator(), threshold, initialValue, accumulator, combiner);
  }

  private static <V, T> V forkJoinReduce(Spliterator<T> spliterator, int threshold, V initialValue,
      BiFunction<T, V, V> accumulator, BinaryOperator<V> combiner) {

    var task = new ReducerTask<>(spliterator, initialValue, threshold, accumulator, combiner);
    return ForkJoinPool//
      .commonPool()
      .invoke(task);
  }


  static <T, V> V sequentialReduce(Spliterator<T> spliterator, V initial,
      BiFunction<T, V, V> accumulator) {

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
      private V acc = initial;
    }

    var box = new Box();
    while (spliterator.tryAdvance(e ->
    {
      box.acc = accumulator.apply(e, box.acc);
    }));
    return box.acc;
  }

}

