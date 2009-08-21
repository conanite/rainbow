package rainbow.functions.system;

import rainbow.functions.Builtin;
import rainbow.types.Rational;
import rainbow.vm.VM;

public class Memory extends Builtin {
  public Memory() {
    super("memory");
  }

  public void invokef(VM vm) {
    long mem = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
    vm.pushA(Rational.make(mem));
  }
}
