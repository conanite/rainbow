package rainbow.vm.instructions;

import rainbow.vm.Instruction;
import rainbow.vm.VM;

public class FinishList extends Instruction {
  public void operate(VM vm) {
    ListBuilder builder = (ListBuilder) vm.popA();
    vm.pushA(builder.list());
  }

  public String toString() {
    return "(finish-list)";
  }
}
