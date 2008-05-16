package rainbow.vm.continuations;

import rainbow.ArcError;
import rainbow.LexicalClosure;
import rainbow.types.ArcObject;
import rainbow.types.Input;
import rainbow.vm.ArcThread;
import rainbow.vm.Continuation;

public class CallWStdinContinuation extends ContinuationSupport {
  private final Input previousInput;

  public CallWStdinContinuation(ArcThread thread, LexicalClosure lc, Continuation caller, Input previousInput) {
    super(thread, lc, caller);
    this.previousInput = previousInput;
  }

  public void onReceive(ArcObject o) {
    thread.swapStdIn(previousInput);
    caller.receive(o);
  }

  public void error(ArcError error) {
    thread.swapStdIn(previousInput);
    super.error(error);
  }
}
