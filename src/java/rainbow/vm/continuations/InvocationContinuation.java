package rainbow.vm.continuations;

import rainbow.LexicalClosure;
import rainbow.types.ArcObject;
import rainbow.types.Pair;
import rainbow.vm.ArcThread;
import rainbow.vm.Continuation;
import rainbow.vm.interpreter.invocation.InvocationComponent;

public class InvocationContinuation extends ContinuationSupport {
  private InvocationComponent invocation;
  public ArcObject function;
  public Pair args;
  public Pair lastArg;

  public InvocationContinuation(ArcThread thread, LexicalClosure lc, Continuation caller, InvocationComponent invocation) {
    super(thread, lc, caller);
    this.invocation = invocation;
  }

  public void continueWith(InvocationComponent invocation) {
    this.invocation = invocation;
    start();
  }

  public void start() {
    invocation.expression.interpret(thread, lc, this);
  }

  protected void onReceive(ArcObject returned) {
    invocation.received(thread, lc, caller, returned, this);
  }

  protected ArcObject getCurrentTarget() {
    return invocation.expression;
  }
}
