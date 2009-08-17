package rainbow.vm.instructions;

import rainbow.types.ArcObject;
import rainbow.vm.Instruction;
import rainbow.vm.VM;

public class FinallyInvoke extends Instruction implements Finally {
  private ArcObject after;

  public FinallyInvoke(ArcObject after) {
    this.after = after;
  }

  public void operate(VM vm) {
    after.invoke(vm, NIL);
  }
}
