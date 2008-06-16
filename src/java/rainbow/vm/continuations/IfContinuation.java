package rainbow.vm.continuations;

import rainbow.vm.Continuation;
import rainbow.vm.ArcThread;
import rainbow.vm.Interpreter;
import rainbow.Truth;
import rainbow.LexicalClosure;
import rainbow.types.Pair;
import rainbow.types.ArcObject;

public class IfContinuation extends ContinuationSupport {
  private Pair args;
  private ArcObject current;

  public IfContinuation(ArcThread thread, LexicalClosure lc, Continuation caller, Pair args) {
    super(thread, lc, caller);
    this.args = args;
  }

  public void onReceive(ArcObject condition) {
    if (args.cdr().isNil()) {
      caller.receive(condition);
    } else if (Truth.isIn(condition)) {
      Interpreter.interpret(thread, lc, caller, args.cdr().car());
    } else {
      args = (Pair) args.cdr().cdr();
      start();
    }
  }

  public void start() {
    if (args.isNil()) {
      caller.receive(args);
    } else {
      current = args.car();
      Interpreter.interpret(thread, lc, (args.cdr().isNil() ? caller : this), current);
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
