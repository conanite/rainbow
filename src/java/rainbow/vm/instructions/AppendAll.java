package rainbow.vm.instructions;

import rainbow.types.ArcObject;
import rainbow.types.Pair;
import rainbow.vm.VM;
import rainbow.vm.Instruction;

public class AppendAll extends Instruction {
  public void operate(VM vm) {
    ArcObject arg = vm.popA();
    ListBuilder builder = (ListBuilder) vm.peekA();
    builder.appendAll((Pair)arg);
  }

  public String toString() {
    return "(append-all)";
  }
}
