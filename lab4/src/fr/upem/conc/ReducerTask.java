//package fr.upem.conc;
//
//import java.util.Arrays;
//import java.util.concurrent.RecursiveTask;
//import java.util.function.BinaryOperator;
//import java.util.function.IntBinaryOperator;
//
//private static class ReducerTask<E> extends RecursiveTask<E> {
//  private final int[] array;
//  private final int startIndex;
//  private final int endIndex;
//  private final int initialValue;
//  private final BinaryOperator<E> operator;
//  public static final int SMALL_LIMIT = 1024;
//
//
//  private ReducerTask(int startIndex, int endIndex, int[] array, int initial,
//      IntBinaryOperator op) {
//    this.array = array;
//    this.operator = op;
//    this.startIndex = startIndex;
//    this.endIndex = endIndex;
//    this.initialValue = initial;
//  }
//
//  //
//  // solve(problem):
//  // if problem is small enough:
//  // solve problem directly (sequential algorithm)
//  // else:
//  // divide the problem in two parts (part1, part2)
//  // fork solve(part1)
//  // solve(part2)
//  // join part1
//  // return combined results
//  @Override
//  protected Integer compute() {
//    if (endIndex - startIndex <= SMALL_LIMIT) {
//      return Arrays.stream(array, startIndex, endIndex).reduce(initialValue, operator);
//    }
//
//    var middle = (startIndex + endIndex) / 2;
//    var part1 = new ReducerTask(startIndex, middle, array, initialValue, operator);
//    var part2 = new ReducerTask(middle, endIndex, array, initialValue, operator);
//
//    part1.fork();
//
//    var result2 = part2.compute();
//    var result1 = part1.join();
//    return operator.applyAsInt(result1, result2);
//  }
//}
