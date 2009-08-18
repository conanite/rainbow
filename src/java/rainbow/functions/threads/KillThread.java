package rainbow.functions.threads;

import rainbow.functions.Builtin;
import rainbow.types.ArcObject;
import rainbow.types.Pair;
import rainbow.vm.VM;
import rainbow.vm.VMInterceptor;

public class KillThread extends Builtin {
  public KillThread() {
    super("kill-thread");
  }

  public ArcObject invoke(Pair args) {
    VM victim = (VM) args.car();
    victim.setInterceptor(VMInterceptor.KILL);
    return NIL;
  }
}
