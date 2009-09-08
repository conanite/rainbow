package rainbow.vm.instructions;

import rainbow.types.Symbol;
import rainbow.vm.Instruction;
import rainbow.vm.VM;

public class StackSym extends Instruction {
  private final Symbol sym;
  private int index;

  public StackSym(Symbol name, int index) {
    this.sym = name;
    this.index = index;
  }

  public void operate(VM vm) {
    vm.pushParam(index);
  }

  public String toString() {
    return "(stack-bound symbol: " + sym + ")";
  }
}
