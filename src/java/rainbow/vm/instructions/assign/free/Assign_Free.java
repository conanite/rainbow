package rainbow.vm.instructions.assign.free;

import rainbow.types.ArcObject;
import rainbow.types.Symbol;
import rainbow.vm.Instruction;
import rainbow.vm.interpreter.BoundSymbol;
import rainbow.vm.interpreter.Quotation;

import java.util.List;

public abstract class Assign_Free extends Instruction {
  protected final Symbol name;

  public Assign_Free(Symbol name) {
    this.name = name;
  }

  public static void addInstructions(List i, Symbol name, ArcObject expr, boolean last) {
    if (expr instanceof BoundSymbol) {
      Assign_Free_Lex.addInstructions(i, name, (BoundSymbol)expr, last);
    } else if (expr instanceof Symbol) {
      Assign_Free_Free.addInstructions(i, name, (Symbol)expr, last);
    } else if (expr.literal()) {
      Assign_Free_Literal.addInstructions(i, name, expr, last);
    } else if (expr instanceof Quotation) {
      Assign_Free_Literal.addInstructions(i, name, ((Quotation) expr).getQuoted(), last);
    } else {
      Assign_Free_Other.addInstructions(i, name, expr, last);
    }
  }
}
