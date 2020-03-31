package fr.umlv.conc;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;
import java.nio.file.Path;

public class Utils {
  private static Path HOME;
  private static final VarHandle HANDLE;
  private static final Object LOCK = new Object();

  static {
    var lookup = MethodHandles.lookup();
    try {
      HANDLE = lookup.findStaticVarHandle(Utils.class, "HOME", Path.class);
    } catch (NoSuchFieldException | IllegalAccessException e) {
      throw new AssertionError(e);
    }
  }

  /*
   * public static Path getHome() { var home = HANDLE.getAcquire(); if (home == null) { synchronized
   * (LOCK) { home = HANDLE.getAcquire(); if (home == null) { System.out.println("init");
   * HANDLE.setRelease(Path.of(System.getenv("HOME"))); } } } return HOME; }
   */



  public static class UtilsHolder {
    private static final Path INSTANCE = Path.of(System.getenv("HOME"));
  }


  public static Path getHome() {
    return UtilsHolder.INSTANCE;
  }

  public static void main(String[] args) {

    var numberThreads = 100;
    var threads = new Thread[numberThreads];

    for (var i = 0; i < threads.length; i++) {
      threads[i] = new Thread(() -> System.out.println(Utils.getHome()));
      threads[i].start();
    }

  }
}
