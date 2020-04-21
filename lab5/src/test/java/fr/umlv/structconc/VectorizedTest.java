package fr.umlv.structconc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import java.util.Arrays;
import java.util.Random;
import java.util.function.IntBinaryOperator;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

@SuppressWarnings("static-method")
public class VectorizedTest {
  private static Stream<Arguments> intsArrayWrapper(IntBinaryOperator op) {
    return IntStream
      .of(0, 1, 10, 100, 1000, 10_000, 100_000)
      .mapToObj(i -> new Random(0).ints(i, 0, 1000).toArray())
      .map(array -> Arguments.of(array, Arrays.stream(array).reduce(0, op)));
  }

  private static Stream<Arguments> sumInt() {
    return intsArrayWrapper(Integer::sum);
  }

  private static Stream<Arguments> minInt() {
    return intsArrayWrapper(Integer::min);
  }

  private static Stream<Arguments> maxInt() {
    return intsArrayWrapper(Integer::max);
  }


  @ParameterizedTest
  @MethodSource("sumInt")
  public void sumLoopTest(int[] array, int expected) {
    assertEquals(expected, Vectorized.sumLoop(array));
  }

  @ParameterizedTest
  @MethodSource("sumInt")
  public void sumReduceLaneTest(int[] array, int expected) {
    assertEquals(expected, Vectorized.sumReduceLane(array));
  }

  @ParameterizedTest
  @MethodSource("sumInt")
  public void sumLanewiseTest(int[] array, int expected) {
    assertEquals(expected, Vectorized.sumLanewise(array));
  }

  @ParameterizedTest
  @MethodSource("sumInt")
  public void differenceLoopTest(int[] array, int expected) {
    assertEquals(-expected, Vectorized.differenceLoop(array));
  }

  @ParameterizedTest
  @MethodSource("sumInt")
  public void differenceLanewiseTest(int[] array, int expected) {
    assertEquals(-expected, Vectorized.differenceLanewise(array));
  }

  @ParameterizedTest
  @MethodSource("minInt")
  public void minLanewiseTest(int[] array, int expected) {
    assertEquals(expected, Vectorized.minmax(array)[0]);
  }

  @ParameterizedTest
  @MethodSource("maxInt")
  public void maxLanewiseTest(int[] array, int expected) {
    // assertEquals(expected, Vectorized.minmax(array)[1]);
  }

}
