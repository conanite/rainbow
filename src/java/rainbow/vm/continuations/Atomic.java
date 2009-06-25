package rainbow.vm.continuations;

import rainbow.ArcError;
import rainbow.LexicalClosure;
import rainbow.types.ArcObject;
import rainbow.vm.ArcThread;
import rainbow.vm.Continuation;

public class Atomic extends ContinuationSupport {
  private static final Object lock = new Object();
  private static ArcThread owner;
  private static int entryCount;

  public Atomic(LexicalClosure lc, Continuation caller) {
    super(lc, caller);
    synchronized (lock) {
      while (thread != owner && owner != null) {
        try {
          lock.wait();
        } catch (InterruptedException e) {
          throw new ArcError("Thread " + Thread.currentThread() + " interrupted: " + e, e);
        }
      }
      owner = thread;
      entryCount++;
    }
  }


  protected void onReceive(ArcObject returned) {
    synchronized (lock) {
      entryCount--;
      if (entryCount == 0) {
        owner = null;
        lock.notifyAll();
      }
    }
    caller.receive(returned);
  }
}
