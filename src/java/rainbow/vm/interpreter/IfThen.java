package rainbow.vm.interpreter;

import rainbow.types.ArcObject;
import rainbow.types.Symbol;
import rainbow.vm.ArcThread;
import rainbow.vm.Continuation;
import rainbow.vm.continuations.ConditionalContinuation;
import rainbow.LexicalClosure;

public class IfThen extends ArcObject implements Conditional {
  public ArcObject ifExpression;
  public ArcObject thenExpression;
  public Conditional next;

  public ArcObject type() {
    return Symbol.make("if-then-clause");
  }

  public void interpret(ArcThread thread, LexicalClosure lc, Continuation caller, Continuation conditional) {
    ifExpression.interpret(lc, conditional);
  }

  public void execute(ArcThread thread, LexicalClosure lc, Continuation caller) {
    thenExpression.interpret(lc, caller);
  }

  public void continueFor(ConditionalContinuation cc, Continuation caller) {
    cc.continueWith(next);
  }

  public void add(Conditional c) {
    if (next != null) {
      next.add(c);
    } else {
      next = c;
    }
  }

  public void take(ArcObject expression) {
    if (ifExpression == null) {
      ifExpression = expression;
    } else if (thenExpression == null) {
      thenExpression = expression;
    } else {
      next.take(expression);
    }
  }
}
