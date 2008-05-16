package rainbow.vm.continuations;

import rainbow.Function;
import rainbow.LexicalClosure;
import rainbow.functions.InterpretedFunction;
import rainbow.functions.Threads;
import rainbow.types.ArcObject;
import rainbow.types.Pair;
import rainbow.vm.ArcThread;
import rainbow.vm.Continuation;
import rainbow.vm.Interpreter;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class ArgumentEvaluator extends ContinuationSupport {
  private Function f;
  private Pair args;
  private List evaluatedArgs;

  public ArgumentEvaluator(ArcThread thread, LexicalClosure lc, Continuation caller, Function f, Pair args) {
    super(thread, lc, caller);
    this.f = f;
    this.args = args;
    evaluatedArgs = new ArrayList(args.size());
  }

  public void start() {
    if (args.isNil()) {
      f.invoke(thread, lc, caller, Pair.buildFrom(evaluatedArgs, ArcObject.NIL));
    } else {
      ArcObject expression = args.car();
      args = (Pair) args.cdr();
      Interpreter.interpret(thread, lc, this, expression);
    }
  }

  public void onReceive(ArcObject o) {
    if (o instanceof InterpretedFunction) {
      evaluatedArgs.add(new Threads.Closure((Function) o, lc));
    } else {
      evaluatedArgs.add(o);
    }
    start();
  }

  public Continuation cloneFor(ArcThread thread) {
    ArgumentEvaluator ae = (ArgumentEvaluator) super.cloneFor(thread);
    ae.args = this.args.copy();
    if (evaluatedArgs != null) {
      ae.evaluatedArgs = new LinkedList(this.evaluatedArgs);
    }
    return ae;
  }
}
