package rainbow.vm.instructions.cond;

import rainbow.LexicalClosure;
import rainbow.Nil;
import rainbow.types.ArcObject;
import rainbow.types.Pair;
import rainbow.vm.VM;
import rainbow.vm.interpreter.BoundSymbol;
import rainbow.vm.interpreter.Conditional;

public class Cond_Lex_Other extends Cond_Lex {
  private final ArcObject thenExpr;
  private final ArcObject elsExpr;
  private final Pair thenInstructions;
  private final Pair elseInstructions;

  public Cond_Lex_Other(BoundSymbol ifExpr, ArcObject thenExpr, Conditional next) {
    super(ifExpr);
    this.thenExpr = thenExpr;
    this.elsExpr = (ArcObject) next;
    this.thenInstructions = Cond.instructionsFor(thenExpr);
    this.elseInstructions = Cond.instructionsFor(elsExpr);
  }

  public void operate(VM vm) {
    LexicalClosure lc = vm.lc();
    if (ifExpr.interpret(lc) instanceof Nil) {
      vm.pushFrame(lc, elseInstructions);
    } else {
      vm.pushFrame(lc, thenInstructions);
    }
  }

  public String toString() {
    return "(cond if:" + ifExpr + " then:" + thenExpr + " else:" + elsExpr + ")";
  }

  public String toString(LexicalClosure lc) {
    return "(cond if:" + ifExpr + " -> " + ifExpr.interpret(lc) + " then:" + thenExpr + " else:" + elsExpr + ")";
  }

}
