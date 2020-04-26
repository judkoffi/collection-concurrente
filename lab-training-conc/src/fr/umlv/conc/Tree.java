package fr.umlv.conc;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.StringJoiner;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;
import java.util.function.BinaryOperator;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * 3. Pourquoi on vous a demandez de faire un Collections.shuffle avant d'insérer les élements dans
 * l'arbre à la question précédente ? Répondez en ajoutant un commentaire dans le code.<br>
 * ==> Pour equilibré l'arbre, eviter d'avoir d'un seule brach qui est occupé
 * 
 */

public class Tree<E extends Comparable<? super E>> {
  static class Node<E extends Comparable<? super E>> {
    private Node<E> left;
    private Node<E> right;
    private E value;
    private int size;

    boolean add(E element) {
      if (value == null) {
        value = element;
        size++;
        return true;
      }
      if (element.equals(value)) {
        return false;
      }
      size++;
      if (element.compareTo(value) < 0) {
        if (left == null) {
          left = new Node<>();
        }
        return left.add(element);
      }
      if (right == null) {
        right = new Node<>();
      }
      return right.add(element);
    }

    void forEach(Consumer<? super E> consumer) {
      if (left != null) {
        left.forEach(consumer);
      }
      if (value != null) {
        consumer.accept(value);
      }
      if (right != null) {
        right.forEach(consumer);
      }
    }

    E reduceSequential(E initialElement, BinaryOperator<E> merger) {
      if (size == 0)
        return initialElement;

      class Box {
        private E acc = initialElement;
      }

      var b = new Box();
      forEach(e ->
      {
        b.acc = merger.apply(e, b.acc);
      });

      return b.acc;
    }
  }

  @SuppressWarnings("serial")
  private static class Reducer<T extends Comparable<? super T>> extends RecursiveTask<T> {
    private final Node<T> tree;
    private final int threshold;
    private final T initialElement;
    private final BinaryOperator<T> merger;

    private Reducer(Node<T> tree, int threshold, T initialElement, BinaryOperator<T> merger) {
      this.tree = tree;
      this.threshold = threshold;
      this.initialElement = initialElement;
      this.merger = merger;
    }

    @Override
    protected T compute() {
      if (tree == null)
        return initialElement;

      if (tree.size <= threshold)
        return tree.reduceSequential(initialElement, merger);

      var part1 = new Reducer<>(tree.left, threshold, initialElement, merger);
      var part2 = new Reducer<>(tree.right, threshold, initialElement, merger);

      part1.fork();

      var result2 = part2.compute();
      var result1 = part1.join();
      var result = merger.apply(result1, result2);
      return merger.apply(tree.value, result);
    }
  }

  private final Node<E> root = new Node<>();

  public boolean add(E element) {
    Objects.requireNonNull(element);
    return root.add(element);
  }

  public void forEach(Consumer<? super E> consumer) {
    Objects.requireNonNull(consumer);
    root.forEach(consumer);
  }

  public E reduceSequential(E initialElement, BinaryOperator<E> merger) {
    Objects.requireNonNull(initialElement);
    Objects.requireNonNull(merger);
    return root.reduceSequential(initialElement, merger);
  }

  public E reduceParallel(int threshold, E initialElement, BinaryOperator<E> merger) {
    Objects.requireNonNull(threshold);
    Objects.requireNonNull(initialElement);
    Objects.requireNonNull(merger);

    Reducer<E> task = new Reducer<E>(root, threshold, initialElement, merger);
    return ForkJoinPool//
      .commonPool()
      .invoke(task);
  }

  @Override
  public String toString() {
    var joiner = new StringJoiner(", ", "[", "]");
    forEach(element -> joiner.add(element.toString()));
    return joiner.toString();
  }

  public static void main(String[] args) {
    Random random = new Random(0);
    List<Integer> values = random.ints(1_000_000, 0, 10_000).boxed().collect(Collectors.toList());

    Collections.shuffle(values);

    Tree<Integer> tree = new Tree<>();
    values.forEach(tree::add);

    int sum = tree.reduceSequential(0, Integer::sum);
    System.out.println("reduceSequential: " + sum);


    int parraledSum = tree.reduceParallel(10_000, 0, Integer::sum);
    System.out.println("parraledSum: " + parraledSum);
  }
}
