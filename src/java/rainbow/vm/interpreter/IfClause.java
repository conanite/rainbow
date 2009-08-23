package rainbow.vm.interpreter;

import rainbow.types.ArcObject;
import rainbow.types.Symbol;

import java.util.List;

public class IfClause extends ArcObject {
  private static final ArcObject TYPE = Symbol.mkSym("if-clause");
  private Conditional first;

  public ArcObject type() {
    return TYPE;
  }

  public void add(Conditional c) {
    if (first != null) {
      first.add(c);
    } else {
      first = c;
    }
  }

  public ArcObject reduce() {
    first = (Conditional) first.reduce();
    if (first instanceof Else) {
      return ((Else)first).ifExpression;
    } else {
      return this;
    }
  }

  public void take(ArcObject expression) {
    first.take(expression);
  }

  public void addInstructions(List i) {
    first.addInstructions(i);
  }

  public String toString() {
    return "(if " + first + ")";
  }

  public int highestLexicalScopeReference() {
    return first.highestLexicalScopeReference();
  }
}
