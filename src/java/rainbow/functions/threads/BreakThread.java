package rainbow.functions.threads;

import rainbow.functions.Builtin;
import rainbow.types.ArcObject;
import rainbow.types.Pair;
import rainbow.vm.VM;
import rainbow.vm.interceptor.VMInterceptor;

public class BreakThread extends Builtin {
  public BreakThread() {
    super("break-thread");
  }

  public ArcObject invoke(Pair args) {
    VM victim = (VM) args.car();
    victim.setInterceptor(VMInterceptor.KILL);
    return NIL;
  }
}
