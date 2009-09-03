package rainbow.vm.instructions;

import rainbow.types.ArcObject;
import rainbow.vm.VM;
import rainbow.vm.Instruction;

public class FinallyInvoke extends Instruction implements Finally {
  private ArcObject after;

  public FinallyInvoke(ArcObject after) {
    this.after = after;
  }

  public void operate(VM vm) {
    after.invoke(vm, NIL);
  }

  public String toString() {
    return "(finally:" + after + ")";
  }
}
