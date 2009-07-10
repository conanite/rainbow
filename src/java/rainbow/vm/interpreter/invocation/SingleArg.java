package rainbow.vm.interpreter.invocation;

import rainbow.LexicalClosure;
import rainbow.types.ArcObject;
import rainbow.types.Pair;
import rainbow.vm.Continuation;
import rainbow.vm.continuations.InvocationContinuation;

public class SingleArg extends InvocationComponent {
  public void received(LexicalClosure lc, Continuation caller, ArcObject arg, InvocationContinuation invocationContinuation) {
    invocationContinuation.function.invoke(lc, caller, new Pair(arg, ArcObject.NIL));
  }


  public String toString() {
    return " " + expression + ")";
  }
}
