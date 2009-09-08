package rainbow.vm.instructions.assign.stack;

import rainbow.types.ArcObject;
import rainbow.types.Symbol;
import rainbow.vm.Instruction;
import rainbow.vm.interpreter.BoundSymbol;
import rainbow.vm.interpreter.Quotation;
import rainbow.vm.interpreter.StackSymbol;

import java.util.List;

public abstract class Assign_Stack extends Instruction {
  protected StackSymbol name;

  public Assign_Stack(StackSymbol name) {
    this.name = name;
  }

  public static void addInstructions(List i, StackSymbol name, ArcObject expr, boolean last) {
    if (expr instanceof BoundSymbol) {
      Assign_Stack_Lex.addInstructions(i, name, (BoundSymbol)expr, last);
    } else if (expr instanceof StackSymbol) {
      Assign_Stack_Stack.addInstructions(i, name, (StackSymbol) expr, last);
    } else if (expr instanceof Symbol) {
      Assign_Stack_Free.addInstructions(i, name, (Symbol)expr, last);
    } else if (expr.literal()) {
      Assign_Stack_Literal.addInstructions(i, name, expr, last);
    } else if (expr instanceof Quotation) {
      Assign_Stack_Literal.addInstructions(i, name, ((Quotation) expr).quoted(), last);
    } else {
      Assign_Stack_Other.addInstructions(i, name, expr, last);
    }
  }
}
