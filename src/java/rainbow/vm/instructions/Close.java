package rainbow.vm.instructions;

import rainbow.functions.Closure;
import rainbow.functions.interpreted.InterpretedFunction;
import rainbow.vm.VM;
import rainbow.vm.Instruction;

public class Close extends Instruction {
  private final InterpretedFunction ifn;

  public Close(InterpretedFunction ifn) {
    this.ifn = ifn;
  }

  public void operate(VM vm) {
    vm.pushA(new Closure(ifn, vm.lc()));
  }

  public String toString() {
    return "(close " + ifn + ")";
  }
}
