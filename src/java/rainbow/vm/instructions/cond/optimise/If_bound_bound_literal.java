package rainbow.vm.instructions.cond.optimise;

import rainbow.Nil;
import rainbow.types.ArcObject;
import rainbow.vm.Instruction;
import rainbow.vm.VM;
import rainbow.vm.interpreter.BoundSymbol;
import rainbow.vm.interpreter.Else;

import java.util.List;

public class If_bound_bound_literal extends Instruction {
  private BoundSymbol ifExpr;
  private BoundSymbol thenExpr;
  private ArcObject elseExpr;

  private If_bound_bound_literal(BoundSymbol ifExpr, BoundSymbol thenExpr, ArcObject elseExpr) {
    this.ifExpr = ifExpr;
    this.thenExpr = thenExpr;
    this.elseExpr = elseExpr;
  }

  public void operate(VM vm) {
    if (ifExpr.interpret(vm.lc()) instanceof Nil) {
      vm.pushA(elseExpr);
    } else {
      vm.pushA(thenExpr.interpret(vm.lc()));
    }
  }

  public static void addInstructions(List i, ArcObject ifExpr, ArcObject thenExpr, ArcObject elseExpr) {
    BoundSymbol ie = (BoundSymbol) ifExpr;
    BoundSymbol te = (BoundSymbol) thenExpr;
    Else e = (Else) elseExpr;
    if (ie.isSameBoundSymbol(te)) {
      if (e.ifExpression instanceof Nil) {
        ie.addInstructions(i);
      } else {
        i.add(new Or(ie, e.ifExpression));
      }
    } else {
      i.add(new If_bound_bound_literal(ie, te, e.ifExpression));
    }
  }

  public String toString() {
    return "(if[bbl] " + ifExpr + " " + thenExpr + " " + elseExpr + ")";
  }

  public static class Or extends Instruction {
    BoundSymbol a;
    ArcObject elseExpr;

    public Or(BoundSymbol a, ArcObject elseExpr) {
      this.a = a;
      this.elseExpr = elseExpr;
    }

    public void operate(VM vm) {
      ArcObject cond = a.interpret(vm.lc());
      if (cond instanceof Nil) {
        vm.pushA(elseExpr);
      } else {
        vm.pushA(cond);
      }
    }

    public String toString() {
      return "(if[bbl$or] " + a + " " + a + " " + elseExpr + ")";
    }
  }
}
