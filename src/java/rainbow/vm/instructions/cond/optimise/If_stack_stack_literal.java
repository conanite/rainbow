package rainbow.vm.instructions.cond.optimise;

import rainbow.Nil;
import rainbow.types.ArcObject;
import rainbow.vm.Instruction;
import rainbow.vm.VM;
import rainbow.vm.interpreter.Else;
import rainbow.vm.interpreter.StackSymbol;

import java.util.List;

public class If_stack_stack_literal extends Instruction {
  private StackSymbol ifExpr;
  private StackSymbol thenExpr;
  private ArcObject elseExpr;

  private If_stack_stack_literal(StackSymbol ifExpr, StackSymbol thenExpr, ArcObject elseExpr) {
    this.ifExpr = ifExpr;
    this.thenExpr = thenExpr;
    this.elseExpr = elseExpr;
  }

  public void operate(VM vm) {
    if (ifExpr.get(vm) instanceof Nil) {
      vm.pushA(elseExpr);
    } else {
      vm.pushA(thenExpr.get(vm));
    }
  }

  public String toString() {
    return "(if " + ifExpr + " " + thenExpr + " " + elseExpr + ")";
  }

  public static void addInstructions(List i, ArcObject ifExpr, ArcObject thenExpr, ArcObject elseExpr) {
    StackSymbol ie = (StackSymbol) ifExpr;
    StackSymbol te = (StackSymbol) thenExpr;
    Else e = (Else) elseExpr;
    if (ie.isSameStackSymbol(te)) {
      if (e.ifExpression instanceof Nil) {
        ie.addInstructions(i);
      } else {
        i.add(new Or(ie, e.ifExpression));
      }
    } else {
      i.add(new If_stack_stack_literal(ie, te, e.ifExpression));
    }
  }

  public static class Or extends Instruction {
    StackSymbol a;
    ArcObject elseExpr;

    public Or(StackSymbol a, ArcObject elseExpr) {
      this.a = a;
      this.elseExpr = elseExpr;
    }

    public void operate(VM vm) {
      ArcObject cond = a.get(vm);
      if (cond instanceof Nil) {
        vm.pushA(elseExpr);
      } else {
        vm.pushA(cond);
      }
    }

    public String toString() {
      return "(if[ssl$or] " + a + " " + a + " " + elseExpr + ")";
    }
  }
}
