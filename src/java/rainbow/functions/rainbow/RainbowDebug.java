package rainbow.functions.rainbow;

import rainbow.functions.Builtin;
import rainbow.types.Pair;
import rainbow.types.Symbol;
import rainbow.vm.VM;
import rainbow.vm.VMInterceptor;

public class RainbowDebug extends Builtin {
  public RainbowDebug() {
    super("rainbow-debug");
  }

  public void invoke(VM vm, Pair args) {
    vm.setInterceptor(VMInterceptor.DEBUG);
    vm.pushA(Symbol.mkSym(name));
  }
}
