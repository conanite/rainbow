package rainbow.functions.threads;

import rainbow.ArcError;
import rainbow.functions.Builtin;
import rainbow.types.ArcObject;
import rainbow.types.Pair;
import rainbow.vm.VM;
import rainbow.vm.Instruction;
import rainbow.vm.instructions.Finally;

public class AtomicInvoke extends Builtin {
  private static final Object lock = new Object();
  private static VM owner;
  private static int entryCount;

  public AtomicInvoke() {
    super("atomic-invoke");
  }

  public void invoke(VM vm, Pair args) {
    ArcObject f = args.car();
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

  public static class ReleaseLock extends Instruction implements Finally {
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
