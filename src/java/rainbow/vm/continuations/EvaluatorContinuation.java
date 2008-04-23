package rainbow.vm.continuations;

import rainbow.vm.Continuation;
import rainbow.vm.ArcThread;
import rainbow.vm.Interpreter;
import rainbow.Bindings;
import rainbow.types.ArcObject;

public class EvaluatorContinuation extends ContinuationSupport {
  public EvaluatorContinuation(ArcThread thread, Bindings namespace, Continuation whatToDo) {
    super(thread, namespace, whatToDo);
  }

  public void digest(ArcObject o) {
    Interpreter.interpret(thread, namespace, whatToDo, o);
  }
}
