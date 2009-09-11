package rainbow.vm.instructions.cond.optimise;

import rainbow.Nil;
import rainbow.types.ArcObject;
import rainbow.types.Pair;
import rainbow.vm.Instruction;
import rainbow.vm.VM;
import rainbow.vm.instructions.cond.Cond;
import rainbow.vm.interpreter.Else;
import rainbow.vm.interpreter.visitor.Visitor;

import java.util.List;

public class If_other_other_literal extends Instruction {
  private Pair thenInstructions;
  private ArcObject thenExpr;
  private ArcObject elseExpr;

  private If_other_other_literal(ArcObject thenExpr, ArcObject elseExpr) {
    this.thenInstructions = Cond.instructionsFor(thenExpr);
    this.thenExpr = thenExpr;
    this.elseExpr = elseExpr;
  }

  public void operate(VM vm) {
    if (vm.popA() instanceof Nil) {
      vm.pushA(elseExpr);
    } else {
      vm.pushConditional(thenInstructions);
    }
  }

  public String toString() {
    return "(if[ool] [other] " + thenExpr + " " + elseExpr + ")";
  }

  public void visit(Visitor v) {
    super.visit(v);
    thenInstructions.visit(v);
  }

  public static void addInstructions(List i, ArcObject ifExpr, ArcObject thenExpr, ArcObject elseExpr) {
    Else e = (Else) elseExpr;
    ifExpr.addInstructions(i);
    i.add(new If_other_other_literal(thenExpr, e.ifExpression));
  }
}
