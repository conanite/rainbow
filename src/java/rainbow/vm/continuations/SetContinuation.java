package rainbow.vm.continuations;

import rainbow.Bindings;
import rainbow.types.ArcObject;
import rainbow.vm.Continuation;
import rainbow.vm.ArcThread;
import rainbow.vm.Interpreter;

public class SetContinuation extends ContinuationSupport {
  private ArcObject value;

  public SetContinuation(ArcThread thread, Bindings namespace, Continuation whatToDo, ArcObject value) {
    super(thread, namespace, whatToDo);
    this.value = value;
  }

  public void digest(ArcObject name) {
    Interpreter.interpret(thread, namespace, new SetSetterContinuation(namespace, whatToDo, name), value);
  }

  protected ArcObject getCurrentTarget() {
    return value;
  }
}
