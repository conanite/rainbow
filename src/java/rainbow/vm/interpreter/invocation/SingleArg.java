package rainbow.vm.interpreter.invocation;

import rainbow.LexicalClosure;
import rainbow.types.ArcObject;
import rainbow.types.Pair;
import rainbow.vm.ArcThread;
import rainbow.vm.Continuation;
import rainbow.vm.continuations.InvocationContinuation;

public class SingleArg extends InvocationComponent {
  public void received(ArcThread thread, LexicalClosure lc, Continuation caller, ArcObject arg, InvocationContinuation invocationContinuation) {
    invocationContinuation.function.invoke(thread, lc, caller, new Pair(arg, ArcObject.NIL));
  }
}
