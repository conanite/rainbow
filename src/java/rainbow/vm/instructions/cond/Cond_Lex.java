package rainbow.vm.instructions.cond;

import rainbow.vm.Instruction;
import rainbow.vm.interpreter.BoundSymbol;
import rainbow.vm.interpreter.Conditional;
import rainbow.vm.interpreter.Quotation;
import rainbow.types.ArcObject;

import java.util.List;

public abstract class Cond_Lex extends Instruction {
  protected final BoundSymbol ifExpr;

  public Cond_Lex(BoundSymbol ifExpr) {
    this.ifExpr = ifExpr;
  }

  public static void addInstructions(List i, BoundSymbol ifExpression, ArcObject thenExpression, Conditional next) {
    if (thenExpression instanceof BoundSymbol) {
      Cond_Lex_Lex.addInstructions(i, ifExpression, (BoundSymbol) thenExpression, next);
    } else if (thenExpression.literal()) {
      i.add(new Cond_Lex_Literal(ifExpression, thenExpression, (ArcObject) next));
    } else if (thenExpression instanceof Quotation) {
      i.add(new Cond_Lex_Literal(ifExpression, ((Quotation) thenExpression).getQuoted(), (ArcObject) next));
    } else {
      i.add(new Cond_Lex_Other(ifExpression, thenExpression, next));
    }
  }
}
