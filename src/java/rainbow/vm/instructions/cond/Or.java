package rainbow.vm.instructions.cond;

import rainbow.LexicalClosure;
import rainbow.Nil;
import rainbow.types.ArcObject;
import rainbow.types.Pair;
import rainbow.vm.VM;
import rainbow.vm.Instruction;
import rainbow.vm.interpreter.BoundSymbol;
import rainbow.vm.interpreter.Conditional;
import rainbow.vm.interpreter.Else;

import java.util.List;

public class Or extends Instruction {
  private final BoundSymbol ifExpr;
  private final ArcObject elseExpr;
  private final Pair elseInstructions;

  public Or(BoundSymbol ifExpr, ArcObject elseExpr) {
    this.ifExpr = ifExpr;
    this.elseExpr = elseExpr;
    this.elseInstructions = Cond.instructionsFor(elseExpr);
  }

  public static void addInstructions(List i, BoundSymbol ifExpression, Conditional next) {
    if (next instanceof Else) {
      ArcObject e = ((Else)next).ifExpression;
      if (e instanceof BoundSymbol) {
        i.add(new Or_Lex(ifExpression, (BoundSymbol)e));
        return;
      } else if (e.literal()) {
        i.add(new Or_Literal(ifExpression, e));
        return;
      }
    }
    i.add(new Or(ifExpression, (ArcObject) next));
  }

  public void operate(VM vm) {
    LexicalClosure lc = vm.lc();
    ArcObject cond = ifExpr.interpret(lc);
    if (cond instanceof Nil) {
      vm.pushFrame(lc, elseInstructions);
    } else {
      vm.pushA(cond);
    }
  }


  public String toString() {
    return "(or:" + ifExpr + " : " + elseExpr + ")";
  }

  public String toString(LexicalClosure lc) {
    return "(or:" + ifExpr + " -> " + ifExpr.interpret(lc) + " : " + elseExpr + ")";
  }
}
