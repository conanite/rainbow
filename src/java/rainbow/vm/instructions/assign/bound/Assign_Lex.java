package rainbow.vm.instructions.assign.bound;

import rainbow.types.ArcObject;
import rainbow.types.Symbol;
import rainbow.vm.Instruction;
import rainbow.vm.interpreter.BoundSymbol;
import rainbow.vm.interpreter.Quotation;
import rainbow.vm.interpreter.StackSymbol;

import java.util.List;

public abstract class Assign_Lex extends Instruction {
  protected final BoundSymbol name;

  public Assign_Lex(BoundSymbol name) {
    this.name = name;
  }

  public static void addInstructions(List i, BoundSymbol name, ArcObject expr, boolean last) {
    if (expr instanceof BoundSymbol) {
      Assign_Lex_Lex.addInstructions(i, name, (BoundSymbol)expr, last);
    } else if (expr instanceof StackSymbol) {
      Assign_Lex_Stack.addInstructions(i, name, (StackSymbol)expr, last);
    } else if (expr instanceof Symbol) {
      Assign_Lex_Free.addInstructions(i, name, (Symbol)expr, last);
    } else if (expr.literal()) {
      Assign_Lex_Literal.addInstructions(i, name, expr, last);
    } else if (expr instanceof Quotation) {
      Assign_Lex_Literal.addInstructions(i, name, ((Quotation) expr).quoted(), last);
    } else {
      Assign_Lex_Other.addInstructions(i, name, expr, last);
    }
  }
}
