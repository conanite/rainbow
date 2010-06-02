package rainbow.vm.instructions.cond.optimise;

import rainbow.Nil;
import rainbow.types.ArcObject;
import rainbow.types.Pair;
import rainbow.vm.Instruction;
import rainbow.vm.VM;
import rainbow.vm.instructions.cond.Cond;
import rainbow.vm.interpreter.StackSymbol;
import rainbow.vm.interpreter.visitor.Visitor;

import java.util.List;

public class If_other_stack_other extends Instruction {
  private StackSymbol thenExpr;
  private Pair elseInstructions;
  private ArcObject elseExpr;

  private If_other_stack_other(StackSymbol thenExpr, ArcObject elseExpr) {
    this.thenExpr = thenExpr;
    this.elseInstructions = Cond.instructionsFor(elseExpr);
    this.elseExpr = elseExpr;
  }

  public void operate(VM vm) {
    if (vm.popA() instanceof Nil) {
      vm.pushConditional(elseInstructions);
    } else {
      vm.pushA(thenExpr.get(vm));
    }
  }

  public String toString() {
    return "(if ? " + thenExpr + " " + elseExpr + ")";
  }

  public void visit(Visitor v) {
    super.visit(v);
    elseInstructions.visit(v);
  }

  public static void addInstructions(List i, ArcObject ifExpr, ArcObject thenExpr, ArcObject elseExpr) {
    ifExpr.addInstructions(i);
    i.add(new If_other_stack_other((StackSymbol) thenExpr, elseExpr));
  }
}
