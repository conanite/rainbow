package rainbow.vm.instructions.cond.optimise;

import rainbow.Nil;
import rainbow.types.ArcObject;
import rainbow.types.Pair;
import rainbow.vm.Instruction;
import rainbow.vm.VM;
import rainbow.vm.instructions.cond.Cond;
import rainbow.vm.interpreter.BoundSymbol;
import rainbow.vm.interpreter.visitor.Visitor;

import java.util.List;

public class If_bound_other_other extends Instruction {
  private BoundSymbol ifExpr;
  private ArcObject thenExpr;
  private Pair thenInstructions;
  private ArcObject elseExpr;
  private Pair elseInstructions;

  private If_bound_other_other(BoundSymbol ifExpr, ArcObject thenExpr, ArcObject elseExpr) {
    this.ifExpr = ifExpr;
    this.thenExpr = thenExpr;
    this.thenInstructions = Cond.instructionsFor(thenExpr);
    this.elseExpr = elseExpr;
    this.elseInstructions = Cond.instructionsFor(elseExpr);
  }

  public void operate(VM vm) {
    if (ifExpr.interpret(vm.lc()) instanceof Nil) {
      vm.pushConditional(elseInstructions);
    } else {
      vm.pushConditional(thenInstructions);
    }
  }

  public String toString() {
    return "(if " + ifExpr + " " + thenExpr + " " + elseExpr + ")";
  }

  public void visit(Visitor v) {
    super.visit(v);
    thenInstructions.visit(v);
    elseInstructions.visit(v);
  }

  public static void addInstructions(List i, ArcObject ifExpr, ArcObject thenExpr, ArcObject elseExpr) {
    BoundSymbol ie = (BoundSymbol) ifExpr;
    i.add(new If_bound_other_other(ie, thenExpr, elseExpr));
  }
}
