package rainbow.vm.continuations;

import rainbow.*;
import rainbow.types.*;
import rainbow.vm.ArcThread;
import rainbow.vm.Continuation;

public class FunctionDispatcher extends ContinuationSupport {
  public static final Symbol TYPE_DISPATCHER_TABLE = (Symbol) Symbol.make("call*");

  private ArcObject args;
  private boolean expectingArg;
  private Function f;
  private Pair evaluatedArgs;
  public ArcObject functionName;

  public FunctionDispatcher(ArcThread thread, LexicalClosure lc, Continuation caller, ArcObject expression) {
    super(thread, lc, caller);
    this.args = expression.cdr();
    this.functionName = expression.car();
  }

  public void process() {
    functionName.interpret(thread, lc, this);
  }

  public void onReceive(ArcObject obj) {
    if (expectingArg) {
      evaluatedArgs = Pair.append(evaluatedArgs, obj);
      startEvaluation();
    } else if (obj instanceof Function) {
      evaluate((Function) obj);
    } else {
      evaluatedArgs = new Pair(Tagged.rep(obj), ArcObject.NIL);
      evaluate((Function) ((Hash) TYPE_DISPATCHER_TABLE.value()).value(obj.type()));
    }
  }

  private void evaluate(Function f) {
    if (args.isNil()) {
      f.invoke(thread, lc, caller, (Pair) args);
    } else {
      expectingArg = true;
      this.f = f;
      continueEvaluation();
    }
  }

  private void startEvaluation() {
    if (args.isNil()) {
      f.invoke(thread, lc, caller, evaluatedArgs);
    } else {
      continueEvaluation();
    }
  }

  private void continueEvaluation() {
    ArcObject expression = args.car();
    args = args.cdr();
    expression.interpret(thread, lc, this);
  }

  public Continuation cloneFor(ArcThread thread) {
    FunctionDispatcher ae = (FunctionDispatcher) super.cloneFor(thread);
    ae.args = this.args.copy();
    ae.evaluatedArgs = this.evaluatedArgs == null ? null : this.evaluatedArgs.copy();
    return ae;
  }

  protected ArcObject getCurrentTarget() {
    return functionName;
  }
}
