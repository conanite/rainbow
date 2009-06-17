package rainbow.vm.interpreter.invocation;

import rainbow.vm.interpreter.invocation.InvocationComponent;
import rainbow.vm.ArcThread;
import rainbow.vm.Continuation;
import rainbow.vm.continuations.InvocationContinuation;
import rainbow.LexicalClosure;
import rainbow.types.ArcObject;

public class FunctionInvocation extends InvocationComponent {
  public void received(ArcThread thread, LexicalClosure lc, Continuation caller, ArcObject function, InvocationContinuation invocationContinuation) {
    invocationContinuation.function = function;
    invocationContinuation.continueWith(next);
  }
}
