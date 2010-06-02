package rainbow.vm.instructions.cond.optimise;

import rainbow.Nil;
import rainbow.types.ArcObject;
import rainbow.vm.Instruction;
import rainbow.vm.VM;
import rainbow.vm.interpreter.BoundSymbol;
import rainbow.vm.interpreter.Else;

import java.util.List;

public class If_bound_literal_literal extends Instruction {
  private BoundSymbol ifExpr;
  private ArcObject thenExpr;
  private ArcObject elseExpr;

  private If_bound_literal_literal(BoundSymbol ifExpr, ArcObject thenExpr, ArcObject elseExpr) {
    this.ifExpr = ifExpr;
    this.thenExpr = thenExpr;
    this.elseExpr = elseExpr;
  }

  public void operate(VM vm) {
    if (ifExpr.interpret(vm.lc()) instanceof Nil) {
      vm.pushA(elseExpr);
    } else {
      vm.pushA(thenExpr);
    }
  }

  public String toString() {
    return "(if " + ifExpr + " " + thenExpr + " " + elseExpr + ")";
  }

  public static void addInstructions(List i, ArcObject ifExpr, ArcObject thenExpr, ArcObject elseExpr) {
    Else e = (Else) elseExpr;
    i.add(new If_bound_literal_literal((BoundSymbol) ifExpr, thenExpr, e.ifExpression));
  }
}
