package rainbow.vm.instructions;

import rainbow.vm.VM;
import rainbow.vm.Instruction;

public class PopArg extends Instruction {
  String name;

  public PopArg(String name) {
    this.name = name;
  }

  public void operate(VM vm) {
    vm.popA();
  }

  public String toString() {
    return "(pop-arg:" + name + ")";
  }
}
