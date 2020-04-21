package fr.umlv.structconc;

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
    var i = 0;
    var sum = 0;
    var limit = array.length - (array.length % SPECIES.length()); // main loop

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
    var i = 0;
    var sum = 0;

    IntVector accumulator = IntVector.zero(SPECIES);

    var limit = array.length - (array.length % SPECIES.length()); // main loop
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

    var i = 0;
    var result = 0;
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

}
