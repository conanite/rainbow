package rainbow.vm.continuations;

import rainbow.LexicalClosure;
import rainbow.types.ArcObject;
import rainbow.vm.ArcThread;
import rainbow.vm.Continuation;
import rainbow.vm.interpreter.Conditional;

public class ConditionalContinuation extends ContinuationSupport {
  private Conditional condition;

  public ConditionalContinuation(ArcThread thread, LexicalClosure lc, Continuation caller) {
    super(thread, lc, caller);
  }

  public void continueWith(Conditional condition) {
    this.condition = condition;
    start();
  }

  public void start() {
    condition.interpret(thread, lc, caller, this);
  }

  protected void onReceive(ArcObject returned) {
    if (returned.isNil()) {
      condition.continueFor(this, caller);
    } else {
      condition.execute(thread, lc, caller);
    }
  }
}
