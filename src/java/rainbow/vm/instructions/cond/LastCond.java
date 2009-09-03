package rainbow.vm.instructions.cond;

import rainbow.Nil;
import rainbow.types.ArcObject;
import rainbow.types.Pair;
import rainbow.vm.VM;
import rainbow.vm.Instruction;

public class LastCond extends Instruction {
  private final ArcObject thenExpr;
  protected final Pair thenInstructions;

  public LastCond(ArcObject thenExpr) {
    this.thenExpr = thenExpr;
    this.thenInstructions = Cond.instructionsFor(thenExpr);
  }

  public void operate(VM vm) {
    if (!(vm.peekA() instanceof Nil)) {
      vm.popA();
      vm.pushFrame(vm.lc(), thenInstructions);
    }
  }

  public String toString() {
    return "(cond then:" + thenExpr + ", else:nil)";
  }
}
