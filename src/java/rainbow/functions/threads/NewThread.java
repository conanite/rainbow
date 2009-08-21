package rainbow.functions.threads;

import rainbow.functions.Builtin;
import rainbow.types.ArcObject;
import rainbow.types.Pair;
import rainbow.vm.VM;
import rainbow.vm.VMInterceptor;

public class NewThread extends Builtin {
  public NewThread() {
    super("new-thread");
  }

  public ArcObject invoke(final Pair args) {
    final VM newVm = new VM();
    new Thread() {
      public void run() {
        args.car().invoke(newVm, NIL);
        try {
          newVm.thread();
        } catch (Throwable t) {
          t.printStackTrace();
        }
      }
    }.start();
    return newVm;
  }
}
