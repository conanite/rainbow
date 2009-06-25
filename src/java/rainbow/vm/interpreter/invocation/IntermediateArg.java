package rainbow.vm.interpreter.invocation;

import rainbow.LexicalClosure;
import rainbow.types.ArcObject;
import rainbow.types.Pair;
import rainbow.vm.Continuation;
import rainbow.vm.continuations.InvocationContinuation;

public class IntermediateArg extends InvocationComponent {
  public void received(LexicalClosure lc, Continuation caller, ArcObject obj, InvocationContinuation invocationContinuation) {
    Pair arg = new Pair(obj, ArcObject.NIL);
    invocationContinuation.lastArg.setCdr(arg);
    invocationContinuation.lastArg = arg;
    invocationContinuation.continueWith(next);
  }
}
