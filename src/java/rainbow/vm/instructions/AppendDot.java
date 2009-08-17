package rainbow.vm.instructions;

import rainbow.vm.Instruction;
import rainbow.vm.VM;
import rainbow.types.ArcObject;

public class AppendDot extends Instruction {
  public void operate(VM vm) {
    ArcObject arg = vm.popA();
    VM.ListBuilder builder = (VM.ListBuilder) vm.peekA();
    builder.last(arg);
  }

  public String toString() {
    return "(append-dot)";
  }
}
