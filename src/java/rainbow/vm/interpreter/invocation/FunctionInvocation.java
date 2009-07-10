package rainbow.vm.interpreter.invocation;

import rainbow.LexicalClosure;
import rainbow.types.ArcObject;
import rainbow.vm.Continuation;
import rainbow.vm.continuations.InvocationContinuation;

public class FunctionInvocation extends InvocationComponent {
  public void received(LexicalClosure lc, Continuation caller, ArcObject function, InvocationContinuation invocationContinuation) {
    invocationContinuation.function = function;
    invocationContinuation.continueWith(next);
  }

  public String toString() {
    return "(" + expression + next;
  }
}
