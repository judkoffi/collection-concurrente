package fr.upem.conc;

import java.util.Random;

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
  }
}
