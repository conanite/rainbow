package rainbow.vm.instructions.cond.optimise;

import rainbow.Nil;
import rainbow.types.ArcObject;
import rainbow.vm.Instruction;
import rainbow.vm.VM;
import rainbow.vm.interpreter.Else;
import rainbow.vm.interpreter.StackSymbol;

import java.util.List;

public class If_other_stack_literal extends Instruction {
  private StackSymbol thenExpr;
  private ArcObject elseExpr;

  private If_other_stack_literal(StackSymbol thenExpr, ArcObject elseExpr) {
    this.thenExpr = thenExpr;
    this.elseExpr = elseExpr;
  }

  public void operate(VM vm) {
    if (vm.popA() instanceof Nil) {
      vm.pushA(elseExpr);
    } else {
      vm.pushA(thenExpr.get(vm));
    }
  }

  public String toString() {
    return "(if[osl] [other] " + thenExpr + " " + elseExpr + ")";
  }

  public static void addInstructions(List i, ArcObject ifExpr, ArcObject thenExpr, ArcObject elseExpr) {
    Else e = (Else) elseExpr;
    ifExpr.addInstructions(i);
    i.add(new If_other_stack_literal((StackSymbol) thenExpr, e.ifExpression));
  }
}
