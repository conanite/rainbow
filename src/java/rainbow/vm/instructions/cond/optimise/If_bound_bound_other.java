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

public class If_bound_bound_other extends Instruction {
  private BoundSymbol ifExpr;
  private BoundSymbol thenExpr;
  private Pair elseInstructions;
  private ArcObject elseExpr;

  private If_bound_bound_other(BoundSymbol ifExpr, BoundSymbol thenExpr, ArcObject elseExpr) {
    this.ifExpr = ifExpr;
    this.thenExpr = thenExpr;
    this.elseInstructions = Cond.instructionsFor(elseExpr);
    this.elseExpr = elseExpr;
  }

  public void operate(VM vm) {
    if (ifExpr.interpret(vm.lc()) instanceof Nil) {
      vm.pushConditional(elseInstructions);
    } else {
      vm.pushA(thenExpr.interpret(vm.lc()));
    }
  }

  public static void addInstructions(List i, ArcObject ifExpr, ArcObject thenExpr, ArcObject elseExpr) {
    BoundSymbol ie = (BoundSymbol) ifExpr;
    BoundSymbol te = (BoundSymbol) thenExpr;
    if (ie.isSameBoundSymbol(te)) {
      i.add(new Or(ie, elseExpr));
    } else {
      i.add(new If_bound_bound_other(ie, te, elseExpr));
    }
  }

  public void visit(Visitor v) {
    super.visit(v);
    elseInstructions.visit(v);
  }

  public String toString() {
    return "(if " + ifExpr + " " + thenExpr + " " + elseExpr + ")";
  }

  public static class Or extends Instruction {
    private BoundSymbol a;
    private Pair elseInstructions;
    private ArcObject elseExpr;

    public Or(BoundSymbol a, ArcObject elseExpr) {
      this.a = a;
      this.elseInstructions = Cond.instructionsFor(elseExpr);
      this.elseExpr = elseExpr;
    }

    public void operate(VM vm) {
      ArcObject cond = a.interpret(vm.lc());
      if (cond instanceof Nil) {
        vm.pushConditional(elseInstructions);
      } else {
        vm.pushA(cond);
      }
    }

    public void visit(Visitor v) {
      super.visit(v);
      elseInstructions.visit(v);
    }

    public String toString() {
      return "(if " + a + " " + a + " " + elseExpr + ")";
    }
  }
}
