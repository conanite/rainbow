package rainbow.vm.continuations;

import rainbow.Bindings;
import rainbow.Function;
import rainbow.functions.InterpretedFunction;
import rainbow.functions.Threads;
import rainbow.vm.Continuation;
import rainbow.vm.ArcThread;
import rainbow.vm.Interpreter;
import rainbow.types.Pair;
import rainbow.types.ArcObject;

import java.util.List;
import java.util.LinkedList;

public class ArgumentEvaluator extends ContinuationSupport {
  private Pair args;
  private List evaluatedArgs = new LinkedList();
  private Pair originalArgs;

  public ArgumentEvaluator(ArcThread thread, Bindings namespace, Continuation whatToDo, Pair args) {
    super(thread, namespace, whatToDo);
    this.args = args;
    this.originalArgs = args;
  }

  public void start() {
    if (args.isNil()) {
      whatToDo.eat(Pair.buildFrom(evaluatedArgs, ArcObject.NIL));
    } else {
      ArcObject expression = args.car();
      args = (Pair) args.cdr();
      Interpreter.interpret(thread, namespace, this, expression);
    }
  }

  public void digest(ArcObject o) {
    if (o instanceof InterpretedFunction) {
      evaluatedArgs.add(new Threads.Closure((Function) o, namespace));
    } else {
      evaluatedArgs.add(o);
    }
    start();
  }

  protected ArcObject getCurrentTarget() {
    return originalArgs;
  }

  public Continuation cloneFor(ArcThread thread) {
    ArgumentEvaluator ae = (ArgumentEvaluator) super.cloneFor(thread);
    ae.args = this.args.copy();
    ae.evaluatedArgs = new LinkedList(this.evaluatedArgs);
    ae.originalArgs = this.originalArgs;
    return ae;
  }

  public String toString() {
    return "ArgumentEvaluator: evaluated " + evaluatedArgs + "; evaluating " + args + " for " + whatToDo;
  }
}
