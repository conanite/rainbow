package rainbow.vm.interpreter;

import rainbow.ArcError;
import rainbow.types.ArcObject;
import rainbow.types.Symbol;
import rainbow.vm.instructions.assign.bound.Assign_Lex;
import rainbow.vm.instructions.assign.free.Assign_Free;

import java.util.List;

public class LastAssignment extends SingleAssignment {

  public void take(ArcObject o) {
    if (name == null) {
      if ((o instanceof Symbol) || (o instanceof BoundSymbol)) {
        name = o;
      } else {
        throw new ArcError("assign: can't assign to " + o);
      }
    } else if (expression == null) {
      expression = o;
    } else {
      throw new ArcError("assign: error: unexpected " + o);
    }
  }

  public String toString() {
    return name + " " + expression;
  }

  public void addInstructions(List i) {
    if (name instanceof BoundSymbol) {
      Assign_Lex.addInstructions(i, (BoundSymbol) name, expression, true);
    } else if (name instanceof Symbol) {
      Assign_Free.addInstructions(i, (Symbol) name, expression, true);
    }
  }

  public int highestLexicalScopeReference() {
    int n = name.highestLexicalScopeReference();
    int e = expression.highestLexicalScopeReference();
    return e > n ? e : n;
  }
}
