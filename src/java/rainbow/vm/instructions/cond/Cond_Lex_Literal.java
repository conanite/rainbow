package rainbow.vm.instructions.cond;

import rainbow.types.ArcObject;
import rainbow.types.Pair;
import rainbow.vm.VM;
import rainbow.vm.interpreter.BoundSymbol;
import rainbow.LexicalClosure;

public class Cond_Lex_Literal extends Cond_Lex {
  private ArcObject thenExpr;
  private ArcObject elseExpr;
  private Pair elseInstructions;

  public Cond_Lex_Literal(BoundSymbol ifExpr, ArcObject thenExpr, ArcObject elseExpr) {
    super(ifExpr);
    this.thenExpr = thenExpr;
    this.elseExpr = elseExpr;
    this.elseInstructions = Cond.instructionsFor(elseExpr);
  }

  public void operate(VM vm) {
    LexicalClosure lc = vm.lc();
    if (ifExpr.interpret(lc).isNil()) {
      vm.pushFrame(lc, elseInstructions);
    } else {
      vm.pushA(thenExpr);
    }
  }

  public String toString() {
    return "(cond if:" + ifExpr + " then:" + thenExpr + " else:" + elseExpr + ")";
  }

  public String toString(LexicalClosure lc) {
    return "(cond if:" + ifExpr + " -> " + ifExpr.interpret(lc) + " then:" + thenExpr + " else:" + elseExpr + ")";
  }

}
