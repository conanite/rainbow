package rainbow.vm.instructions;

import rainbow.vm.Instruction;
import rainbow.vm.VM;
import rainbow.vm.VM.ListBuilder;

public class NewList extends Instruction {
  public void operate(VM vm) {
    vm.pushA(new ListBuilder());
  }

  public String toString() {
    return "(new-list)";
  }
}
