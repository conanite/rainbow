package rainbow.functions.threads;

import rainbow.functions.Builtin;
import rainbow.vm.VM;
import rainbow.types.Pair;

public class CurrentThread extends Builtin {
  public CurrentThread() {
    super("current-thread");
  }

  public void invoke(VM vm, Pair args) {
    vm.pushA(vm);
  }
}
