package rainbow.vm.interpreter;

import rainbow.types.ArcObject;
import rainbow.types.Symbol;
import rainbow.vm.Continuation;
import rainbow.vm.continuations.ConditionalContinuation;
import rainbow.LexicalClosure;

public class IfClause extends ArcObject {
  private static final ArcObject TYPE = Symbol.make("if-clause");
  private Conditional first;

  public ArcObject type() {
    return TYPE;
  }

  public void interpret(LexicalClosure lc, Continuation caller) {
    new ConditionalContinuation(lc, caller).continueWith(first);
  }

  public void add(Conditional c) {
    if (first != null) {
      first.add(c);
    } else {
      first = c;
    }
  }

  public void take(ArcObject expression) {
    first.take(expression);
  }
}
