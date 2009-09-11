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

public class If_other_bound_other extends Instruction {
  private BoundSymbol thenExpr;
  private Pair elseInstructions;
  private ArcObject elseExpr;

  private If_other_bound_other(BoundSymbol thenExpr, ArcObject elseExpr) {
    this.elseInstructions = Cond.instructionsFor(elseExpr);
    this.elseExpr = elseExpr;
    this.thenExpr = thenExpr;
  }

  public void operate(VM vm) {
    if (vm.popA() instanceof Nil) {
      vm.pushConditional(elseInstructions);
    } else {
      vm.pushA(thenExpr.interpret(vm.lc()));
    }
  }

  public String toString() {
    return "(if[obo] [other] " + thenExpr + " " + elseExpr + ")";
  }

  public void visit(Visitor v) {
    super.visit(v);
    elseInstructions.visit(v);
  }

  public static void addInstructions(List i, ArcObject ifExpr, ArcObject thenExpr, ArcObject elseExpr) {
    ifExpr.addInstructions(i);
    i.add(new If_other_bound_other((BoundSymbol) thenExpr, elseExpr));
  }
}
