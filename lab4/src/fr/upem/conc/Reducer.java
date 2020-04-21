
package fr.upem.conc;

import java.util.Arrays;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;
import java.util.function.IntBinaryOperator;

public class Reducer {
  private Reducer() {}

  @SuppressWarnings("serial")
  private static class IntegerReducerTask extends RecursiveTask<Integer> {
    private final int[] array;
    private final int startIndex;
    private final int endIndex;
    private final int initialValue;
    private final IntBinaryOperator operator;
    public static final int THRESHOLD = 1024;


    private IntegerReducerTask(int startIndex, int endIndex, int[] array, int initial,
        IntBinaryOperator op) {
      this.array = array;
      this.operator = op;
      this.startIndex = startIndex;
      this.endIndex = endIndex;
      this.initialValue = initial;
    }

    //
    // solve(problem):
    // if problem is small enough:
    // solve problem directly (sequential algorithm)
    // else:
    // divide the problem in two parts (part1, part2)
    // fork solve(part1)
    // solve(part2)
    // join part1
    // return combined results
    @Override
    protected Integer compute() {
      if (endIndex - startIndex <= THRESHOLD) {
        return Arrays.stream(array, startIndex, endIndex).reduce(initialValue, operator);
      }

      var middle = (startIndex + endIndex) / 2;
      var part1 = new IntegerReducerTask(startIndex, middle, array, initialValue, operator);
      var part2 = new IntegerReducerTask(middle, endIndex, array, initialValue, operator);

      part1.fork();

      var result2 = part2.compute();
      var result1 = part1.join();
      return operator.applyAsInt(result1, result2);
    }
  }

  public static int sum(int[] array) {
    var sum = 0;
    for (var value : array) {
      sum += value;
    }
    return sum;
  }

  public static int max(int[] array) {
    var max = Integer.MIN_VALUE;
    for (var value : array) {
      max = Math.max(max, value);
    }
    return max;
  }

  public static int sumReduce(int[] array) {
    return reduce(array, Integer.MIN_VALUE, Integer::sum);
  }

  public static int maxReduce(int[] array) {
    return reduce(array, Integer.MIN_VALUE, Integer::max);
  }

  public static int sumReduceStrem(int[] array) {
    return reduceWithStream(array, Integer.MIN_VALUE, Integer::sum);
  }

  public static int maxReduceStream(int[] array) {
    return reduceWithStream(array, Integer.MIN_VALUE, Integer::max);
  }

  public static int sumWithStreamParallel(int[] array) {
    return parallelReduceWithStream(array, Integer.MIN_VALUE, Integer::sum);
  }

  public static int maxReduceStreamParallel(int[] array) {
    return parallelReduceWithStream(array, Integer.MIN_VALUE, Integer::max);
  }

  public static int sumReduceStreamParallelForkJoin(int[] array) {
    return parallelReduceWithForkJoin(array, Integer.MIN_VALUE, Integer::sum);
  }

  public static int maxReduceStreamParallelForkJoin(int[] array) {
    return parallelReduceWithForkJoin(array, Integer.MIN_VALUE, Integer::max);
  }


  /******************************************************************************************/

  private static int reduce(int[] array, int initial, IntBinaryOperator op) {
    var acc = initial;
    for (var value : array) {
      acc = op.applyAsInt(acc, value);
    }
    return acc;
  }

  private static int reduceWithStream(int[] array, int initial, IntBinaryOperator op) {
    return Arrays.stream(array).reduce(initial, op);
  }

  private static int parallelReduceWithStream(int[] array, int initial, IntBinaryOperator op) {
    return Arrays.stream(array).parallel().reduce(initial, op);
  }

  private static int parallelReduceWithForkJoin(int[] array, int initial, IntBinaryOperator op) {
    return ForkJoinPool//
      .commonPool()
      .invoke(new IntegerReducerTask(0, array.length, array, initial, op));
  }


}
