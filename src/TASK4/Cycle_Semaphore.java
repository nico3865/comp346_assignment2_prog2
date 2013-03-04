package TASK4;

public class Cycle_Semaphore extends Semaphore {

	private int num_of_threads_in_cycle;

	public Cycle_Semaphore(int i, int j) {
		super(i);
		num_of_threads_in_cycle = j;
	}

	public synchronized void Wait() { // the original semaphore Wait() function that never goes under zero.
		while (this.value <= 0) {
			try {
				wait();
			} catch (InterruptedException e) {
				System.out
						.println("Semaphore::Wait() - caught InterruptedException: "
								+ e.getMessage());
				e.printStackTrace();
			}
		}
		this.value--;
	}

	public synchronized void Signal() {
		++this.value;
		notify();
	}

	public synchronized boolean DoneCycle() {
		// if both threads in the group ran then
		// it's time to signal to the other group of threads.
		return this.value == 0;
	}

	public synchronized void Reset() {
		this.value = num_of_threads_in_cycle;
		notify();
	}

}
