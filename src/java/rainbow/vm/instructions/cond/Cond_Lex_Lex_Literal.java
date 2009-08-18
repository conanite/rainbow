package rainbow.vm.instructions.cond;

import rainbow.LexicalClosure;
import rainbow.types.ArcObject;
import rainbow.vm.VM;
import rainbow.vm.interpreter.BoundSymbol;

import java.util.List;

public class Cond_Lex_Lex_Literal extends Cond_Lex {
  private final BoundSymbol then;
  private final ArcObject els;

  public Cond_Lex_Lex_Literal(BoundSymbol ifExpr, BoundSymbol then, ArcObject els) {
    super(ifExpr);
    this.then = then;
    this.els = els;
  }

  public void operate(VM vm) {
    LexicalClosure lc = vm.lc();
    if (ifExpr.interpret(lc).isNil()) {
      vm.pushA(els);
    } else {
      vm.pushA(then.interpret(lc));
    }
  }

  public String toString() {
    return "(cond lex:" + ifExpr + " lex:" + then + " lit:" + els + ")";
  }

  public static void addInstructions(List i, BoundSymbol ifExpression, BoundSymbol thenExpression, ArcObject elseExpression) {
    if ((ifExpression.isSameBoundSymbol(thenExpression)) && elseExpression.isNil()) {
      ifExpression.addInstructions(i);
    } else {
      i.add(new Cond_Lex_Lex_Literal(ifExpression, thenExpression, elseExpression));
    }
  }
}
