package rainbow.vm.instructions.cond;

import rainbow.LexicalClosure;
import rainbow.Nil;
import rainbow.types.ArcObject;
import rainbow.types.Pair;
import rainbow.vm.VM;
import rainbow.vm.interpreter.BoundSymbol;
import rainbow.vm.interpreter.Conditional;
import rainbow.vm.interpreter.Else;
import rainbow.vm.interpreter.Quotation;

import java.util.List;

public class Cond_Lex_Lex extends Cond_Lex {
  private final BoundSymbol thenExpr;
  private final ArcObject elsExpr;
  private final Pair elseInstructions;

  public Cond_Lex_Lex(BoundSymbol ifExpr, BoundSymbol thenExpr, Conditional elsExpr) {
    super(ifExpr);
    this.thenExpr = thenExpr;
    this.elsExpr = (ArcObject) elsExpr;
    elseInstructions = Cond.instructionsFor(this.elsExpr);
  }

  public void operate(VM vm) {
    LexicalClosure lc = vm.lc();
    if (ifExpr.interpret(lc) instanceof Nil) {
      vm.pushFrame(lc, elseInstructions);
    } else {
      vm.pushA(thenExpr.interpret(lc));
    }
  }

  public String toString() {
    return "(cond if:" + ifExpr + " then:" + thenExpr + " else:" + elsExpr + ")";
  }

  public String toString(LexicalClosure lc) {
    return "(cond if:" + ifExpr + " -> " + ifExpr.interpret(lc) + " then:" + thenExpr + " -> " + thenExpr.interpret(lc) + " else:" + elsExpr + ")";
  }

  public static void addInstructions(List i, BoundSymbol ifExpression, BoundSymbol thenExpression, Conditional next) {
    if (next instanceof Else) {
      if (((Else) next).ifExpression.literal()) {
        Cond_Lex_Lex_Literal.addInstructions(i, ifExpression, thenExpression, ((Else)next).ifExpression);
      } else if (((Else)next).ifExpression instanceof Quotation) {
        Cond_Lex_Lex_Literal.addInstructions(i, ifExpression, thenExpression, ((Quotation) ((Else) next).ifExpression).quoted());
      } else {
        i.add(new Cond_Lex_Lex(ifExpression, thenExpression, next));
      }
    } else if (ifExpression.isSameBoundSymbol(thenExpression)) {
      Or.addInstructions(i, ifExpression, next);
    } else {
      i.add(new Cond_Lex_Lex(ifExpression, thenExpression, next));
    }
  }
}
