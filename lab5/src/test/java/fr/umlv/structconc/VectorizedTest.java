package fr.umlv.structconc;

import static java.lang.Integer.MAX_VALUE;
import static java.lang.Integer.MIN_VALUE;
import static java.util.stream.IntStream.of;
import static org.junit.jupiter.api.Assertions.assertEquals;
import java.util.Arrays;
import java.util.Random;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

@SuppressWarnings("static-method")
public class VectorizedTest {
  private static Stream<Arguments> provideIntArrays() {
    return IntStream
      .of(0, 1, 10, 100, 1000, 10_000, 100_000)
      .mapToObj(i -> new Random(0).ints(i, 0, 1000).toArray())
      .map(array -> Arguments.of(array, Arrays.stream(array).reduce(0, Integer::sum)));
  }

  private static Stream<Arguments> provideIntsArrayMinMax() {
    return IntStream
      .of(0, 1, 10, 100, 1000, 10_000, 100_000)
      .mapToObj(i -> new Random(0).ints(i, 0, 1000).toArray())
      .map(array -> Arguments
        .of(array,
            new int[] {of(array).min().orElse(MAX_VALUE), of(array).max().orElse(MIN_VALUE)}));
  }

  @ParameterizedTest
  @MethodSource("provideIntArrays")
  public void sumLoopTest(int[] array, int expected) {
    assertEquals(expected, Vectorized.sumLoop(array));
  }

  @ParameterizedTest
  @MethodSource("provideIntArrays")
  public void sumReduceLaneTest(int[] array, int expected) {
    assertEquals(expected, Vectorized.sumReduceLane(array));
  }

  @ParameterizedTest
  @MethodSource("provideIntArrays")
  public void sumLanewiseTest(int[] array, int expected) {
    assertEquals(expected, Vectorized.sumLanewise(array));
  }

  @ParameterizedTest
  @MethodSource("provideIntArrays")
  public void differenceLoopTest(int[] array, int expected) {
    assertEquals(-expected, Vectorized.differenceLoop(array));
  }

  @ParameterizedTest
  @MethodSource("provideIntArrays")
  public void differenceLanewiseTest(int[] array, int expected) {
    assertEquals(-expected, Vectorized.differenceLanewise(array));
  }

  @ParameterizedTest
  @MethodSource("provideIntsArrayMinMax")
  public void minVectorTest(int[] array, int[] expected) {
    assertEquals(expected[0], Vectorized.minmax(array)[0]);
    assertEquals(expected[1], Vectorized.minmax(array)[1]);
  }

  @ParameterizedTest
  @MethodSource("provideIntsArrayMinMax")
  public void minLoopTest(int[] array, int[] expected) {
    assertEquals(expected[0], Vectorized.minStream(array));

    assertEquals(expected[1], Vectorized.maxStream(array));
  }

}
