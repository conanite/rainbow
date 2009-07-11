package rainbow.vm.interpreter;

import rainbow.types.ArcObject;
import rainbow.types.Symbol;
import rainbow.vm.ArcThread;
import rainbow.vm.Continuation;
import rainbow.vm.continuations.ConditionalContinuation;
import rainbow.LexicalClosure;
import rainbow.ArcError;

public class LastIfThen extends ArcObject implements Conditional {
  public ArcObject ifExpression;
  public ArcObject thenExpression;

  public ArcObject type() {
    return Symbol.make("last-if-then-clause");
  }

  public void interpret(ArcThread thread, LexicalClosure lc, Continuation caller, Continuation conditional) {
    ifExpression.interpret(lc, conditional);
  }

  public void execute(ArcThread thread, LexicalClosure lc, Continuation caller) {
    thenExpression.interpret(lc, caller);
  }

  public void continueFor(ConditionalContinuation conditionalContinuation, Continuation caller) {
    caller.receive(NIL);
  }

  public void add(Conditional c) {
    throw new ArcError("Internal error: if clause: unexpected extra condition: " + c);
  }

  public void take(ArcObject expression) {
    if (ifExpression == null) {
      ifExpression = expression;
    } else if (thenExpression == null) {
      thenExpression = expression;
    } else {
      throw new ArcError("Internal error: if clause: unexpected: " + expression);
    }
  }

  public String toString() {
    return ifExpression + " " + thenExpression;
  }
}
