package rainbow.vm.continuations;

import rainbow.vm.Continuation;
import rainbow.vm.ArcThread;
import rainbow.vm.Interpreter;
import rainbow.Bindings;
import rainbow.Truth;
import rainbow.types.Pair;
import rainbow.types.ArcObject;

public class IfContinuation extends ContinuationSupport {
  private Pair args;
  private ArcObject current;

  public IfContinuation(ArcThread thread, Bindings namespace, Continuation whatToDo, Pair args) {
    super(thread, namespace, whatToDo);
    this.args = args;
  }

  public void digest(ArcObject condition) {
    if (args.cdr().isNil()) {
      whatToDo.eat(condition);
    } else if (Truth.isIn(condition)) {
      Interpreter.interpret(thread, namespace, whatToDo, args.cdr().car());
    } else {
      args = (Pair) args.cdr().cdr();
      start();
    }
  }

  public void start() {
    if (args.isNil()) {
      whatToDo.eat(args);
    } else {
      current = args.car();
      Interpreter.interpret(thread, namespace, this, current);
    }
  }

  protected ArcObject getCurrentTarget() {
    return current;
  }

  public Continuation cloneFor(ArcThread thread) {
    IfContinuation e = (IfContinuation) super.cloneFor(thread);
    e.args = this.args.copy();
    return e;
  }
}
