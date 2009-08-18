package rainbow.vm.continuations;

import rainbow.ArcError;
import rainbow.types.ArcObject;
import rainbow.vm.Instruction;
import rainbow.vm.VM;
import rainbow.vm.instructions.Finally;

public class Atomic {
  private static final Object lock = new Object();
  private static VM owner;
  private static int entryCount;

  public static void invoke(VM vm, ArcObject f) {
    synchronized (lock) {
      while (vm != owner && owner != null) {
        try {
          lock.wait();
        } catch (InterruptedException e) {
          throw new ArcError("Thread " + Thread.currentThread() + " interrupted: " + e, e);
        }
      }
      owner = vm;
      entryCount++;
      vm.pushFrame(new ReleaseLock());
    }

    f.invoke(vm, ArcObject.NIL);
  }

  private static class ReleaseLock extends Instruction implements Finally {
    public void operate(VM vm) {
      synchronized (lock) {
        entryCount--;
        if (entryCount == 0) {
          owner = null;
          lock.notifyAll();
        }
      }
    }
  }
}
