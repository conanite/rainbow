package rainbow.vm.instructions;

import rainbow.vm.VM;
import rainbow.vm.Instruction;

public class Listify extends Instruction {
  int size;

  public Listify(int size) {
    this.size = size;
  }

  public void operate(VM vm) {
    vm.pushA(vm.popArgs(size));
  }

  public String toString() {
    return "(listify " + size + ")";
  }
}
