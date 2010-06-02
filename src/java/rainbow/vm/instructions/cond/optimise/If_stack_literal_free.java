package rainbow.vm.instructions.cond.optimise;

import rainbow.Nil;
import rainbow.types.ArcObject;
import rainbow.types.Symbol;
import rainbow.vm.Instruction;
import rainbow.vm.VM;
import rainbow.vm.interpreter.Else;
import rainbow.vm.interpreter.StackSymbol;

import java.util.List;

public class If_stack_literal_free extends Instruction {
  private StackSymbol ifExpr;
  private ArcObject thenExpr;
  private Symbol elseExpr;

  private If_stack_literal_free(StackSymbol ifExpr, ArcObject thenExpr, Symbol elseExpr) {
    this.ifExpr = ifExpr;
    this.thenExpr = thenExpr;
    this.elseExpr = elseExpr;
  }

  public void operate(VM vm) {
    if (ifExpr.get(vm) instanceof Nil) {
      vm.pushA(elseExpr.value());
    } else {
      vm.pushA(thenExpr);
    }
  }

  public String toString() {
    return "(if " + ifExpr + " " + thenExpr + " " + elseExpr + ")";
  }

  public static void addInstructions(List i, ArcObject ifExpr, ArcObject thenExpr, ArcObject elseExpr) {
    StackSymbol ie = (StackSymbol) ifExpr;
    Else e = (Else) elseExpr;
    Symbol ee = (Symbol) e.ifExpression;
    i.add(new If_stack_literal_free(ie, thenExpr, ee));
  }
}
