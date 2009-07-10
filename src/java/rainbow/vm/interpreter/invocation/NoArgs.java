package rainbow.vm.interpreter.invocation;

import rainbow.LexicalClosure;
import rainbow.types.ArcObject;
import rainbow.vm.Continuation;
import rainbow.vm.continuations.InvocationContinuation;

public class NoArgs extends InvocationComponent {
  public void received(LexicalClosure lc, Continuation caller, ArcObject function, InvocationContinuation invocationContinuation) {
    function.invoke(lc, caller, ArcObject.NIL);
  }

  public String toString() {
    return "(" + expression + ")";
  }
}
