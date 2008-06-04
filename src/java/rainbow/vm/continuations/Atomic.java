package rainbow.vm.continuations;

import rainbow.vm.ArcThread;
import rainbow.vm.Continuation;
import rainbow.types.ArcObject;
import rainbow.LexicalClosure;
import rainbow.ArcError;

public class Atomic extends ContinuationSupport {
  private static final Object lock = new Object();
  private static ArcThread owner;
  private static int entryCount;

  public Atomic(ArcThread thread, LexicalClosure lc, Continuation caller) {
    super(thread, lc, caller);
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
