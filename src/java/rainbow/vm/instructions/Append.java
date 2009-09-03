package rainbow.vm.instructions;

import rainbow.types.ArcObject;
import rainbow.vm.VM;
import rainbow.vm.Instruction;

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
