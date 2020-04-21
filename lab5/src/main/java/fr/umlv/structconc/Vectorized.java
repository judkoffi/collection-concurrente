package fr.umlv.structconc;

import java.util.Arrays;
import jdk.incubator.vector.IntVector;
import jdk.incubator.vector.VectorOperators;
import jdk.incubator.vector.VectorSpecies;

public class Vectorized {
  public static int sumLoop(int[] array) {
    var sum = 0;
    for (var value : array) {
      sum += value;
    }
    return sum;
  }

  private static final VectorSpecies<Integer> SPECIES = IntVector.SPECIES_PREFERRED;

  public static int sumReduceLane(int[] array) {
    int i = 0;
    int sum = 0;
    int limit = array.length - (array.length % SPECIES.length()); // main loop

    for (; i < limit; i += SPECIES.length()) {
      var vector = IntVector.fromArray(SPECIES, array, i);
      sum += vector.reduceLanes(VectorOperators.ADD); // Ajout de la somme partiel a sum
    }

    for (; i < array.length; i++) { // post loop
      sum += array[i];
    }
    return sum;
  }


  public static int sumLanewise(int[] array) {
    int i = 0;
    int sum = 0;

    IntVector accumulator = IntVector.zero(SPECIES);

    int limit = array.length - (array.length % SPECIES.length()); // main loop
    for (; i < limit; i += SPECIES.length()) {
      var vector = IntVector.fromArray(SPECIES, array, i);
      accumulator = accumulator.add(vector);
    }

    for (; i < array.length; i++) { // post loop
      sum += array[i];
    }

    sum += accumulator.reduceLanes(VectorOperators.ADD);
    return sum;
  }

  public static int differenceLoop(int[] array) {
    return -sumLoop(array);
  }

  public static int differenceLanewise(int[] array) {
    if (array.length == 0)
      return 0;

    int i = 0;
    int result = 0;
    IntVector accumulator = IntVector.zero(SPECIES);

    var limit = array.length - (array.length % SPECIES.length()); // main loop
    for (; i < limit; i += SPECIES.length()) {
      var vector = IntVector.fromArray(SPECIES, array, i);
      accumulator = accumulator.sub(vector);
    }

    for (; i < array.length; i++) { // post loop
      result += array[i];
    }
    result -= accumulator.reduceLanes(VectorOperators.ADD);
    return -result;
  }

  public static int[] minmax(int[] array) {
    IntVector minVector = IntVector.zero(SPECIES);
    IntVector maxVector = IntVector.zero(SPECIES);

    var i = 0;
    var limit = array.length - (array.length % SPECIES.length()); // main loop

    for (; i < limit; i += SPECIES.length()) {
      var vector = IntVector.fromArray(SPECIES, array, i);
      minVector = minVector.min(vector);
      maxVector = maxVector.max(vector);
    }

    int min = minVector.reduceLanes(VectorOperators.ADD);
    int max = maxVector.reduceLanes(VectorOperators.ADD);

    for (; i < array.length; i++) { // post loop
      if (array[i] < min) {
        min = array[i];
      }

      if (array[i] > max) {
        max = array[i];
      }
    }

    return new int[] {min, max};
  }


  public static int minStream(int[] array) {
    return Arrays.stream(array).min().getAsInt();
  }

  public static int maxStream(int[] array) {
    return Arrays.stream(array).max().getAsInt();
  }


  public static void main(String[] args) {
    var a = new int[] {1, 5, 1, 3, 0};
    var v = minmax(a);

    System.out.println("min: " + v[0]);
    System.out.println("max: " + v[1]);

    var b = Arrays.stream(a).max().getAsInt();
    System.out.println("stream " + b);
  }
}
