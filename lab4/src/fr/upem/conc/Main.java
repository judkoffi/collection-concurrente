package fr.upem.conc;

import java.util.Random;
import java.util.stream.Collectors;

public class Main {
  public static void main(String[] args) {
    var random = new Random(10);
    var array = random.ints(1_000_000, 0, 1000).toArray();


    System.out.println("------ v1 -------");
    System.out.println(Reducer.sum(array));
    System.out.println(Reducer.max(array));


    System.out.println("------ v2 -------");
    System.out.println(Reducer.sumReduce(array));
    System.out.println(Reducer.maxReduce(array));


    System.out.println("------ v3 -------");
    System.out.println(Reducer.sumReduceStrem(array));
    System.out.println(Reducer.maxReduceStream(array));

    System.out.println("------ v4 -------");
    System.out.println(Reducer.sumWithStreamParallel(array));
    System.out.println(Reducer.maxReduceStreamParallel(array));

    System.out.println("------ v5 -------");
    System.out.println(Reducer.sumReduceStreamParallelForkJoin(array));
    System.out.println(Reducer.maxReduceStreamParallelForkJoin(array));


    var list = random.ints(20_000, 0, 1000).boxed().collect(Collectors.toList());

    // sequential
    System.out.println("Sequential reduce");
    System.out
      .println(ForkJoinCollections.sequentialReduce(list.spliterator(), 0, (acc, value) -> acc + value));

    System.out.println("Stream sum");
    System.out.println(list.stream().mapToInt(m -> m).sum());

    System.out.println("Fork join");
    // fork/join
    // var list = IntStream.range(0, 10_000).boxed().collect(Collectors.toList());
    System.out
      .println(ForkJoinCollections.forkJoinReduce(list, 10_000, 0, (acc, value) -> acc + value, (acc1, acc2) -> acc1 + acc2));

  }
}
