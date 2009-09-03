package rainbow.vm.instructions.cond;

import rainbow.types.ArcObject;
import rainbow.types.Pair;
import rainbow.vm.VM;
import rainbow.vm.Instruction;
import rainbow.Nil;

import java.util.ArrayList;
import java.util.List;

public class Cond extends Instruction {
  private final ArcObject thenExpr;
  private final ArcObject elseExpr;
  private final Pair thenInstructions;
  private final Pair elseInstructions;

  public Cond(ArcObject thenExpr, ArcObject elseExpr) {
    this.thenExpr = thenExpr;
    this.thenInstructions = instructionsFor(thenExpr);
    this.elseExpr = elseExpr;
    this.elseInstructions = instructionsFor(elseExpr);
  }

  public static Pair instructionsFor(ArcObject expr) {
    List list = new ArrayList();
    expr.addInstructions(list);
    return Pair.buildFrom(list);
  }

  public void operate(VM vm) {
    ArcObject arg = vm.popA();
    if (arg instanceof Nil) {
      vm.pushFrame(vm.lc(), elseInstructions);
    } else {
      vm.pushFrame(vm.lc(), thenInstructions);
    }
  }

  public String toString() {
    return "(cond then:" + thenExpr + ", else:" + elseExpr + ")";
  }
}
