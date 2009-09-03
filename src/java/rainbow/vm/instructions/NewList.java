package rainbow.vm.instructions;

import rainbow.vm.VM;
import rainbow.vm.Instruction;

public class NewList extends Instruction {
  public void operate(VM vm) {
    vm.pushA(new ListBuilder());
  }

  public String toString() {
    return "(new-list)";
  }
}
