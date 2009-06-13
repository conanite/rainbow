package rainbow.vm.continuations;

import rainbow.LexicalClosure;
import rainbow.types.ArcObject;
import rainbow.types.Pair;
import rainbow.vm.ArcThread;
import rainbow.vm.Continuation;
import rainbow.vm.Interpreter;

public class IfContinuation extends ContinuationSupport {
  private ArcObject args;

  public IfContinuation(ArcThread thread, LexicalClosure lc, Continuation caller, Pair args) {
    super(thread, lc, caller);
    this.args = args;
    start();
  }

  public void onReceive(ArcObject condition) {
    if (!condition.isNil()) {
      Interpreter.interpret(thread, lc, caller, args.cdr().car());
    } else {
      args = args.cdr().cdr();
      start();
    }
  }

  public void start() {
    if (args.isNil()) {
      caller.receive(args);
    } else {
      Interpreter.interpret(thread, lc, (args.cdr().isNil() ? caller : this), args.car());
    }
  }

  protected ArcObject getCurrentTarget() {
    return args.car();
  }

  public Continuation cloneFor(ArcThread thread) {
    IfContinuation e = (IfContinuation) super.cloneFor(thread);
    e.args = this.args.copy();
    return e;
  }
}
