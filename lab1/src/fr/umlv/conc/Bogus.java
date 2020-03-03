package fr.umlv.conc;

/*
 * 1. Le programme lance un thread qui execute une méthode qui incrémenter une variable tant que le boolean stop est a false
 * , puis demande au main de mettre en pause le thread courant.
 * Après la pause, elle essaie de mettre le boolean a true et affiche la valeur.
 * 
 * 2. Le programme fait une boucle infini et le sysout ne s'effectue pas.
 * Cela est du a une optimisation de la JVM qui stock dans un registe la valeur du boolean pour  eviter d'aller la relire en RAM.
 * Donc même quand le boolean est setter a true, le thread qui execute runCounter ne voit pas la valeur mise à jour.
 *
 *localCounter ==> 63134437
 */

public class Bogus {
	private boolean stop;
	private final Object lock = new Object();

	public void runCounter() {
		var localCounter = 0;
		for (;;) {
			synchronized (lock) {
				if (stop) {
					break;
				}
			}
			localCounter++;
		}
		System.out.println(localCounter);
	}

	public void stop() {
		synchronized (lock) {
			stop = true;
		}
	}

	public static void main(String[] args) throws InterruptedException {
		var bogus = new Bogus();
		var thread = new Thread(bogus::runCounter);
		thread.start();
		Thread.sleep(100);
		bogus.stop();
		thread.join();
	}
}
