package fr.umlv.conc;

import java.util.OptionalDouble;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 2)<br>
 * Par exemple, si deux threads essaient d’accéder au champ <i>party<i>, ils peuvent le voir avant
 * qu’il soit initialisé par le paramètre <i>party<i> du constructeur. le champs party n'est pas
 * final<br>
 * l'acces auxchamps sum et count ne sont pas protéger par un verrou lors d'un acces concourant
 * 
 * 4)<br>
 * Ajouter un commentaire dans le main pour expliquer à quoi sert l'appel à Thread.onSpinWait().
 * <br>
 * On utilise onSpinWait() qui permet d’éviter l'attente actif, et ne pas faire chauffer le CPU
 * lorsque un thread tente de prendre un lock<br>
 */
public class AverageVote {
  private final int party;
  private long sum;
  private int count;

  private ReentrantLock lock = new ReentrantLock();
  private Condition condition = lock.newCondition();

  public AverageVote(int party) {
    this.party = party;
  }

  public void vote(int value) throws InterruptedException {
    lock.lock();
    try {
      sum += value;
      count++;
      condition.signal();
    } finally {
      lock.unlock();
    }
  }


  public OptionalDouble average() {
    lock.lock();
    try {
      var result = count == 0 //
          ? OptionalDouble.empty() //
          : OptionalDouble.of(sum / (double) count);

      condition.signal();
      return result;
    } finally {
      lock.unlock();
    }
  }



  public static void main(String[] args) {
    var vote = new AverageVote(5_000);
    new Thread(() ->
    {
      for (var start = System.currentTimeMillis(); System.currentTimeMillis() - start < 10_000;) {
        Thread.onSpinWait();
        vote.average().ifPresent(average ->
        {
          if (average != 256.0) {
            throw new AssertionError("average " + average);
          }
        });
      }
      System.out.println("end !");
    }).start();

    for (var i = 0; i < 5_000; i++) {
      new Thread(() ->
      {
        try {
          vote.vote(256);
        } catch (InterruptedException e) {
          return;
        }
        System.out.println(vote.average().orElseThrow());
      }).start();
    }
  }
}
