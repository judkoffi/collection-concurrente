package fr.umlv.conc;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;

// Tips volatile:
/*
 * Toujours lire les volatile avant de lire les autres champs, cela garantie que les champs seront lu
 * en RAM
 */

/*
 * Toujours ecrire les autres champs avant les volatiles, cela garantie que les champs seront écrits
 * en RAM
 */

public class ReentrantSpinLock {
  private volatile int lock;
  private Thread ownerThread;

  private static final VarHandle LOCK_HANDLE;

  static {
    var lookup = MethodHandles.lookup();
    try {
      LOCK_HANDLE = lookup.findVarHandle(ReentrantSpinLock.class, "lock", int.class);
    } catch (NoSuchFieldException | IllegalAccessException e) {
      throw new AssertionError(e);
    }
  }

  public void lock() {
    // idée de l'algo
    // on récupère la thread courante
    // si lock est == à 0, on utilise un CAS pour le mettre à 1 et
    // on sauvegarde la thread qui possède le lock dans ownerThread.
    // sinon on regarde si la thread courante n'est pas ownerThread,
    // si oui alors on incrémente lock.
    //
    // et il faut une boucle pour retenter le CAS après avoir appelé onSpinWait()


    var currentThread = Thread.currentThread();

    for (;;) {
      if (LOCK_HANDLE.compareAndSet(this, 0, 1)) {
        ownerThread = currentThread;
        return;
      }

      if (ownerThread == currentThread) {
        lock++;
        return;
      }
      Thread.onSpinWait();
    }

  }

  public void unlock() {
    // idée de l'algo
    // si la thread courante est != ownerThread, on pète une exception
    // si lock == 1, on remet ownerThread à null
    // on décrémente lock

    if (Thread.currentThread() != ownerThread) {
      throw new IllegalStateException();
    }

    var tmp = lock;// volatille read
    if (tmp == 1) {
      ownerThread = null;
      lock = 0; // volatille write
      return;
    }

    lock = tmp - 1; // volatille write
  }

  public static void main(String[] args) throws InterruptedException {
    var runnable = new Runnable() {
      private int counter;
      private final ReentrantSpinLock spinLock = new ReentrantSpinLock();

      @Override
      public void run() {
        for (var i = 0; i < 1_000_000; i++) {
          spinLock.lock();
          try {
            spinLock.lock();
            try {
              counter++;
            } finally {
              spinLock.unlock();
            }
          } finally {
            spinLock.unlock();
          }
        }
      }
    };
    var t1 = new Thread(runnable);
    var t2 = new Thread(runnable);
    t1.start();
    t2.start();
    t1.join();
    t2.join();
    System.out.println("counter " + runnable.counter);
  }
}
