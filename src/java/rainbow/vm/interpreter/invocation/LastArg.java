package rainbow.vm.interpreter.invocation;

import rainbow.LexicalClosure;
import rainbow.types.ArcObject;
import rainbow.types.Pair;
import rainbow.vm.ArcThread;
import rainbow.vm.Continuation;
import rainbow.vm.continuations.InvocationContinuation;

public class LastArg extends InvocationComponent {
  public void received(ArcThread thread, LexicalClosure lc, Continuation caller, ArcObject arg, InvocationContinuation invocationContinuation) {
    invocationContinuation.lastArg.setCdr(new Pair(arg, ArcObject.NIL));
    invocationContinuation.function.invoke(thread, lc, caller, invocationContinuation.args);
  }
}
