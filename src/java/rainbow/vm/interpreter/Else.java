package rainbow.vm.interpreter;

import rainbow.types.ArcObject;
import rainbow.types.Symbol;
import rainbow.vm.ArcThread;
import rainbow.vm.Continuation;
import rainbow.vm.continuations.ConditionalContinuation;
import rainbow.LexicalClosure;
import rainbow.ArcError;

public class Else extends ArcObject implements Conditional {
  public ArcObject ifExpression;

  public ArcObject type() {
    return Symbol.make("else-clause");
  }

  public void interpret(ArcThread thread, LexicalClosure lc, Continuation caller, Continuation conditional) {
    ifExpression.interpret(lc, caller);
  }

  public void execute(ArcThread thread, LexicalClosure lc, Continuation caller) {
  }

  public void continueFor(ConditionalContinuation conditionalContinuation, Continuation caller) {
  }

  public void add(Conditional c) {
    throw new ArcError("Internal error: if clause: unexpected extra condition: " + c);
  }

  public void take(ArcObject expression) {
    if (ifExpression == null) {
      ifExpression = expression;
    } else {
      throw new ArcError("Internal error: if clause: unexpected: " + expression);
    }
  }
}
