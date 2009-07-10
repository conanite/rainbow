package rainbow.vm.interpreter.invocation;

import rainbow.ArcError;
import rainbow.LexicalClosure;
import rainbow.types.ArcObject;
import rainbow.vm.Continuation;
import rainbow.vm.continuations.InvocationContinuation;

public abstract class InvocationComponent {
  public ArcObject expression;
  protected InvocationComponent next;

  public abstract void received(LexicalClosure lc, Continuation caller, ArcObject function, InvocationContinuation invocationContinuation);

  public void take(ArcObject expression) {
    if (this.expression == null) {
      this.expression = expression;
    } else if (next != null) {
      next.take(expression);
    } else {
      throw new ArcError("invocation builder: internal error: unexpected " + expression);
    }
  }

  public void add(InvocationComponent ic) {
    if (next == null) {
      next = ic;
    } else {
      next.add(ic);
    }
  }
}
