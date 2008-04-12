package rainbow.vm.continuations;

import rainbow.Bindings;
import rainbow.ArcError;
import rainbow.types.ArcObject;
import rainbow.types.Input;
import rainbow.vm.Continuation;
import rainbow.vm.ArcThread;

public class CallWStdinContinuation extends ContinuationSupport {
  private final Input previousInput;

  public CallWStdinContinuation(ArcThread thread, Bindings namespace, Continuation whatToDo, Input previousInput) {
    super(thread, namespace, whatToDo);
    this.previousInput = previousInput;
  }

  public void digest(ArcObject o) {
    thread.swapStdIn(previousInput);
    whatToDo.eat(o);
  }

  public void error(ArcError error) {
    thread.swapStdIn(previousInput);
    super.error(error);
  }
}
