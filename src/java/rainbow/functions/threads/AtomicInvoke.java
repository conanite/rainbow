package rainbow.functions.threads;

import rainbow.functions.Builtin;
import rainbow.types.Pair;
import rainbow.vm.VM;
import rainbow.vm.continuations.Atomic;

public class AtomicInvoke extends Builtin {
  public AtomicInvoke() {
    super("atomic-invoke");
  }

  public void invoke(VM vm, Pair args) {
    Atomic.invoke(vm, args.car());
  }
}
