package rainbow.vm.continuations;

import rainbow.vm.Continuation;
import rainbow.vm.ArcThread;
import rainbow.types.ArcObject;
import rainbow.types.Output;
import rainbow.ArcError;
import rainbow.Bindings;

public class CallWStdoutContinuation extends ContinuationSupport {
  private Output previousOutput;

  public CallWStdoutContinuation(ArcThread thread, Bindings namespace, Continuation whatToDo, Output previousOutput) {
    super(thread, namespace, whatToDo);
    this.previousOutput = previousOutput;
  }

  public void digest(ArcObject o) {
    thread.swapStdOut(previousOutput);
    whatToDo.eat(o);
  }

  public void error(ArcError error) {
    thread.swapStdOut(previousOutput);
    super.error(error);
  }
}
