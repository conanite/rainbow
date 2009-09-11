package rainbow.vm.instructions.cond.optimise;

import rainbow.Nil;
import rainbow.types.ArcObject;
import rainbow.types.Pair;
import rainbow.vm.Instruction;
import rainbow.vm.VM;
import rainbow.vm.instructions.cond.Cond;
import rainbow.vm.interpreter.BoundSymbol;
import rainbow.vm.interpreter.Else;
import rainbow.vm.interpreter.visitor.Visitor;

import java.util.List;

public class If_bound_other_literal extends Instruction {
  private BoundSymbol ifExpr;
  private ArcObject thenExpr;
  private Pair thenInstructions;
  private ArcObject elseExpr;

  private If_bound_other_literal(BoundSymbol ifExpr, ArcObject thenExpr, ArcObject elseExpr) {
    this.ifExpr = ifExpr;
    this.thenExpr = thenExpr;
    this.thenInstructions = Cond.instructionsFor(thenExpr);
    this.elseExpr = elseExpr;
  }

  public void operate(VM vm) {
    if (ifExpr.interpret(vm.lc()) instanceof Nil) {
      vm.pushA(elseExpr);
    } else {
      vm.pushConditional(thenInstructions);
    }
  }

  public String toString() {
    return "(if[bol] " + ifExpr + " " + thenExpr + " " + elseExpr + ")";
  }

  public void visit(Visitor v) {
    super.visit(v);
    thenInstructions.visit(v);
  }

  public static void addInstructions(List i, ArcObject ifExpr, ArcObject thenExpr, ArcObject elseExpr) {
    BoundSymbol ie = (BoundSymbol) ifExpr;
    Else e = (Else) elseExpr;
    i.add(new If_bound_other_literal(ie, thenExpr, e.ifExpression));
  }
}
