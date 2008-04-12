package rainbow.vm.continuations;

import rainbow.vm.Continuation;
import rainbow.vm.ArcThread;
import rainbow.vm.Interpreter;
import rainbow.Bindings;
import rainbow.functions.InterpretedFunction;
import rainbow.types.Pair;
import rainbow.types.ArcObject;

public class FunctionEvaluator extends ContinuationSupport {
  private InterpretedFunction f;
  private Pair body;

  public FunctionEvaluator(ArcThread thread, Bindings namespace, Continuation whatToDo, InterpretedFunction f, Pair body) {
    super(thread, namespace, whatToDo);
    this.f = f;
    this.body = body;
  }

  public void digest(ArcObject o) {
    ArcObject expression = body.car();
    body = (Pair) body.cdr();
    Continuation next = body.isNil() ? whatToDo : this;
    Interpreter.interpret(thread, namespace, next, expression);
  }

  protected ArcObject getCurrentTarget() {
    return f;
  }

  public Continuation cloneFor(ArcThread thread) {
    FunctionEvaluator e = (FunctionEvaluator) super.cloneFor(thread);
    e.body = body.copy();
    return e;
  }
}
