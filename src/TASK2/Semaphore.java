package TASK2;
// Source code for semaphore class:

class Semaphore {
	private int value;

	public Semaphore(int value) {
		this.value = value;
	}

	public Semaphore() {
		this(0);
	}

	public synchronized void Wait() {
		this.value--;// TASK 1: 1/ I decremented the semaphore's value at the
						// beginning of the method instead of the end: because
						// if it's done at the end, a lot of threads could be
						// waiting and the semaphore value would not be
						// reflecting the number of waiting threads--it would
						// only reflect the number of threads that acquired an
						// authorization (and in fact would never go under
						// zero).
		if (this.value < 0) { // TASK 1: 2/ I changed while statement to an if
								// statement to prevent a process from waiting
								// again if more threads are on the waiting list
								// (i.e. negative semaphore value). Now, all
								// threads that entered this if statement are on
								// "the waiting list" and can be picked to run
								// if another thread emits a notify() for this
								// object.
								//
								// 3/ I also changed "<= 0" to "< 0", because 0
								// means that it was 1 (i.e. semaphore is free)
								// at the beginning of the function--the current
								// process just made it 0 with the first
								// decrement statement.
			try {
				wait(); // TASK 1: comment - any thread that reached this point
						// is on the waiting list. If the while statement was
						// kept it would have resulted in a deadlock--because
						// any thread, upon notification, would run back into
						// the while loop and wait() again since the semaphore's
						// value is still negative even if it was just
						// incremented by the Signal() function.
			} catch (InterruptedException e) {
				System.out
						.println("Semaphore::Wait() - caught InterruptedException: "
								+ e.getMessage());
				e.printStackTrace();
			}
		}

	}

	public synchronized void Signal() {
		++this.value;
		notify();
	}

	public synchronized void P() {
		this.Wait();
	}

	public synchronized void V() {
		this.Signal();
	}
}
