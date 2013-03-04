package TASK4;

import CharStackExceptions.CharStackEmptyException;
import CharStackExceptions.CharStackFullException;
import CharStackExceptions.CharStackInvalidAceessException;
import CharStackExceptions.*;

// Source code for stack manager:

public class StackManager2 {
	// The Stack
	private static CharStack stack = new CharStack();
	private static final int NUM_ACQREL = 4; // Number of Producer/Consumer
												// threads
	private static final int NUM_PROBERS = 1; // Number of threads dumping stack
	private static int iThreadSteps = 3; // Number of steps they take

	// Semaphore declarations. Insert your code in the following:
	// ...
	// ...

	// TASK 2:
	private static Semaphore semaphore = new Semaphore(1);// 1 because only
															// one
															// thread can access
															// the stack at a
															// time

	// TASK 4:
	// start with producers' lock available. --> Two producers can run at a time
	private static Semaphore producer_semaphore = new Cycle_Semaphore(2, 2);
	private static int counter_of_running_PRODUCER_threads = 0;

	// start with consumers blocked
	private static Semaphore consumer_semaphore = new Cycle_Semaphore(0, 2);
	private static int counter_of_running_CONSUMER_threads = 0;

	// The main()
	public static void main(String[] argv) {
		// Some initial stats...
		try {
			System.out.println("Main thread starts executing.");
			System.out
					.println("Initial value of top = " + stack.getTop() + ".");
			System.out.println("Initial value of stack top = " + stack.pick()
					+ ".");
			System.out.println("Main thread will now fork several threads.");
		} catch (CharStackEmptyException e) {
			System.out.println("Caught exception: StackCharEmptyException");
			System.out.println("Message : " + e.getMessage());
			System.out.println("Stack Trace : ");
			e.printStackTrace();
		}
		/*
		 * The birth of threads
		 */
		Consumer ab1 = new Consumer();
		Consumer ab2 = new Consumer();
		System.out.println("Two Consumer threads have been created.");
		Producer rb1 = new Producer();
		Producer rb2 = new Producer();
		System.out.println("Two Producer threads have been created.");
		CharStackProber csp = new CharStackProber();
		System.out.println("One CharStackProber thread has been created.");
		/*
		 * start executing
		 */
		ab1.start();
		rb1.start();
		ab2.start();
		rb2.start();
		csp.start();
		/*
		 * Wait by here for all forked threads to die
		 */
		try {
			ab1.join();
			ab2.join();
			rb1.join();
			rb2.join();
			csp.join();
			// Some final stats after all the child threads terminated...
			System.out.println("System terminates normally.");
			System.out.println("Final value of top = " + stack.getTop() + ".");
			System.out.println("Final value of stack top = " + stack.pick()
					+ ".");
			System.out.println("Final value of stack top-1 = "
					+ stack.getAt(stack.getTop() - 1) + ".");
			// System.out.println("Stack access count = "
			// + stack.getAccessCounter());
		} catch (InterruptedException e) {
			System.out
					.println("Caught InterruptedException: " + e.getMessage());
			System.exit(1);
		} catch (Exception e) {
			System.out.println("Caught exception: " + e.getClass().getName());
			System.out.println("Message : " + e.getMessage());
			System.out.println("Stack Trace : ");
			e.printStackTrace();
		}
	} // main()

	/*
	 * Inner Consumer thread class
	 */
	static class Consumer extends BaseThread {
		private char copy; // A copy of a block returned by pop()

		public void run() {

			System.out.println("Consumer thread [TID=" + this.iTID
					+ "] starts executing.");

			try {
				// each thread grabs a lock until completion of all 6 loops: (2
				// are allowed to run at any time)
				consumer_semaphore.Wait();

				counter_of_running_PRODUCER_threads = 0; //are now not running anymore
				counter_of_running_CONSUMER_threads++; // count consumer threads
														// in
				// order to know when we are
				// done this round

				for (int i = 0; i < StackManager2.iThreadSteps; i++) {
					// Insert your code in the following:
					// ...
					// ...
					try {
						semaphore.Wait(); // enter critical section:
						this.copy = CharStack.pop(); // pop top of stack
						System.out.println("Consumer thread [TID=" + this.iTID
								+ "] pops character =" + this.copy);

					} catch (CharStackEmptyException fe) {
						// fe.printStackTrace();
						// i--; // redo this iteration next time (after a
						// Signal()
						// and Wait()) since it was
						// "missed"
						System.out.println("11");

					} catch (Exception e) {
						e.printStackTrace();
						// i--; // redo this iteration next time since it was
						// "missed"
						System.out.println("12");

					} finally {
						semaphore.Signal(); // end of critical section
					}

				}
			} catch (Exception e) {

			} finally {
				// at the end of the 6 consumer loops, increment the mutex
				// producer_semaphore only if the 2 consumer threads ran:
				if (counter_of_running_CONSUMER_threads >= 2) {
					producer_semaphore.Signal();
					producer_semaphore.Signal();
				}

				System.out.println("Consumer thread [TID=" + this.iTID
						+ "] terminates.");
			}

		}
	} // class Consumer

	/*
	 * Inner class Producer
	 */
	static class Producer extends BaseThread {
		private char block; // block to be returned

		public void run() {

			System.out.println("Producer thread [TID=" + this.iTID
					+ "] starts executing.");

			try {
				// each thread grabs a lock until its completion of all 6 loops:
				// (2 are allowed to run at any time)
				producer_semaphore.Wait();

				counter_of_running_CONSUMER_threads = 0; // not the running group anymore
				counter_of_running_PRODUCER_threads++; // count consumer threads
														// in
														// order to know when we
														// are
														// done this round

				for (int i = 0; i < StackManager2.iThreadSteps; i++) {
					// Insert your code in the following:
					// ...
					// ...
					try {
						semaphore.Wait(); // enter critical section:
						char top = CharStack.pick(); // read the top char in
														// stack:
						this.block = (char) (top + 1); // get the next character
														// in
														// the (ascii) alphabet.
						CharStack.push(this.block); // push it onto the stack

						System.out.println("Producer thread [TID=" + this.iTID
								+ "] pushes character =" + this.block);

					} catch (CharStackEmptyException ee) {
						this.block = 'a'; // if the stack is empty at pick()
											// then
											// the char 'a' must be written
						System.out.println("3");

						try {
							CharStack.push(this.block); // push it onto the
														// stack
						} catch (CharStackFullException e) {
							// i--; // redo this iteration next time (after a
							// Signal() and Wait()) since it was
							// "missed"
							System.out.println("1");
						}

					} catch (CharStackFullException e) {
						// i--; // redo this iteration next time since it was
						// "missed"
						System.out.println("2");
						
					} catch (Exception e) {
						e.printStackTrace();
					} finally {
						semaphore.Signal(); // end of critical section
					}
				}
			} catch (Exception e) {

			} finally {
				// at the end of 6 loops of the producer, increment the
				// consumer_semaphore twice only if the 2 producer
				// threads ran:
				if (counter_of_running_PRODUCER_threads >= 2) {
					consumer_semaphore.Signal();
					consumer_semaphore.Signal();
				}

				System.out.println("Producer thread [TID=" + this.iTID
						+ "] terminates.");
			}

		}
	} // class Producer

	/*
	 * Inner class CharStackProber to dump stack contents
	 */
	static class CharStackProber extends BaseThread {
		public void run() {
			System.out.println("CharStackProber thread [TID=" + this.iTID
					+ "] starts executing.");
			for (int i = 0; i < 2 * StackManager2.iThreadSteps; i++) {
				// Insert your code in the following. Note that the stack state
				// must be
				// printed in the required format.
				// ...
				// ...
				try {
					semaphore.Wait(); // enter the critical section:
					System.out.println("Stack S = ([" + stack.getAt(0) + "],["
							+ stack.getAt(1) + "],[" + stack.getAt(2) + "],["
							+ stack.getAt(3) + "],[" + stack.getAt(4) + "],["
							+ stack.getAt(5) + "],[" + stack.getAt(6) + "],["
							+ stack.getAt(7) + "],[" + stack.getAt(8) + "],["
							+ stack.getAt(9) + "])");
				} catch (CharStackInvalidAceessException e) {
					e.printStackTrace();
				} finally {
					semaphore.Signal(); // end of critical section
				}

				// TASK 2: IMPORTANT NOTE - as it stands now the CharStackProber
				// csp thread's 6 loop iterations could execute at any time in
				// the threads order of execution (and often they execute last,
				// all 6 of them). That
				// seems to me to be a problem: we would like to probe between
				// every--but it's not
				// written in the question and the code that we have to take
				// care of this. A more meaningful architecture would have been
				// to trigger the stack probing routine not within its own
				// thread but rather as a part of the Producer and Consumer
				// threads (called from within them). But again such a
				// modification is not requested in the question, and by chance
				// probing occasionally executes between other threads in some
				// sample runs.
			}
		}
	} // class CharStackProber
} // class StackManager
