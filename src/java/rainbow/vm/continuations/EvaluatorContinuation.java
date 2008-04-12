package rainbow.vm.continuations;

import rainbow.vm.Continuation;
import rainbow.vm.ArcThread;
import rainbow.vm.Interpreter;
import rainbow.Bindings;
import rainbow.types.ArcObject;

public class EvaluatorContinuation extends ContinuationSupport {
  private ArcObject sourceInvocation;

  public EvaluatorContinuation(ArcThread thread, Bindings namespace, Continuation whatToDo, ArcObject functionName) {
    super(thread, namespace, whatToDo);
    sourceInvocation = functionName;
  }

  public void digest(ArcObject o) {
    Interpreter.interpret(thread, namespace, whatToDo, o);
  }

  protected ArcObject getCurrentTarget() {
    return sourceInvocation;
  }
}
