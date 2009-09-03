package rainbow.vm.instructions;

import rainbow.vm.VM;
import rainbow.vm.Instruction;
import rainbow.types.ArcObject;

public class AppendDot extends Instruction {
  public void operate(VM vm) {
    ArcObject arg = vm.popA();
    ListBuilder builder = (ListBuilder) vm.peekA();
    builder.last(arg);
  }

  public String toString() {
    return "(append-dot)";
  }
}
