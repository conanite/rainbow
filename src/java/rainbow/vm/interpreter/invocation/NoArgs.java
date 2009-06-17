package rainbow.vm.interpreter.invocation;

import rainbow.LexicalClosure;
import rainbow.types.ArcObject;
import rainbow.vm.ArcThread;
import rainbow.vm.Continuation;
import rainbow.vm.continuations.InvocationContinuation;

public class NoArgs extends InvocationComponent {
  public void received(ArcThread thread, LexicalClosure lc, Continuation caller, ArcObject function, InvocationContinuation invocationContinuation) {
    function.invoke(thread, lc, caller, ArcObject.NIL);
  }
}
