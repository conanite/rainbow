package rainbow.vm.instructions.cond;

import rainbow.types.ArcObject;
import rainbow.types.Pair;
import rainbow.vm.Instruction;
import rainbow.vm.VM;

import java.util.ArrayList;
import java.util.List;

public class LastCond extends Instruction {
  private final ArcObject thenExpr;
  private final Pair thenInstructions;

  public LastCond(ArcObject thenExpr) {
    this.thenExpr = thenExpr;
    this.thenInstructions = instructionsFor(thenExpr);
  }

  private Pair instructionsFor(ArcObject expr) {
    List list = new ArrayList();
    expr.addInstructions(list);
    return Pair.buildFrom(list);
  }

  public void operate(VM vm) {
    if (!vm.peekA().isNil()) {
      vm.popA();
      vm.pushFrame(vm.lc(), thenInstructions);
    }
  }

  public String toString() {
    return "(cond then:" + thenExpr + ", else:nil)";
  }
}
