package rainbow.vm.instructions;

import rainbow.types.ArcObject;
import rainbow.vm.Instruction;
import rainbow.vm.VM;

public class Append extends Instruction {
  public void operate(VM vm) {
    ArcObject arg = vm.popA();
    ListBuilder builder = (ListBuilder) vm.peekA();
    builder.append(arg);
  }

  public String toString() {
    return "(append)";
  }
}
