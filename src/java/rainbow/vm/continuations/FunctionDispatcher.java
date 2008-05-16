package rainbow.vm.continuations;

import rainbow.vm.Continuation;
import rainbow.vm.ArcThread;
import rainbow.vm.Interpreter;
import rainbow.*;
import rainbow.functions.InterpretedFunction;
import rainbow.functions.Threads;
import rainbow.types.ArcObject;
import rainbow.types.Pair;

import java.util.List;
import java.util.ArrayList;
import java.util.LinkedList;

public class FunctionDispatcher extends ContinuationSupport {
  private static final Object EXPECT_FUNCTION = new Object();
  private static final Object EXPECT_ARGUMENT = new Object();
  public static final boolean ALLOW_MACRO_EXPANSION = false;
  private ArcObject args;
  private Object state = EXPECT_FUNCTION;
  private Function f;
  private List evaluatedArgs;
  private ArcObject functionName;

  public FunctionDispatcher(ArcThread thread, LexicalClosure lc, Continuation caller, ArcObject expression) {
    super(thread, lc, caller);
    this.args = expression.cdr();
    this.functionName = expression.car();
  }

  public void process() {
    Interpreter.interpret(thread, lc, this, functionName);
  }

  public void onReceive(ArcObject obj) {
    if (state == EXPECT_FUNCTION) {
      digestFunction(obj);
    } else if (state == EXPECT_ARGUMENT) {
      digestArgument(obj);
    }
  }

  private void digestFunction(ArcObject fn) {
    if (fn instanceof SpecialForm) {
      ((SpecialForm) fn).invoke(thread, lc, caller, (Pair) args);
    } else if (fn instanceof Function) {
      evaluate((Function) fn);
    } else {
      throw new ArcError("Expected a function : got " + fn + " with args " + args);
    }
  }

  private void evaluate(Function f) {
    if (args.isNil()) {
      f.invoke(thread, lc, caller, (Pair) args);
    } else {
      state = EXPECT_ARGUMENT;
      this.f = f;
      evaluatedArgs = new ArrayList(((Pair) args).size());
      startEvaluation();
    }
  }

  private void startEvaluation() {
    if (args.isNil()) {
      f.invoke(thread, lc, caller, Pair.buildFrom(evaluatedArgs, ArcObject.NIL));
    } else {
      ArcObject expression = args.car();
      args = args.cdr();
      Interpreter.interpret(thread, lc, this, expression);
    }
  }

  public void digestArgument(ArcObject o) {
    if (o instanceof InterpretedFunction) {
      System.out.println("FD.digestArgument: we should never get in here, isn't this handled by Interpreter???");
      evaluatedArgs.add(new Threads.Closure((Function) o, lc));
    } else {
      evaluatedArgs.add(o);
    }
    startEvaluation();
  }

  public Continuation cloneFor(ArcThread thread) {
    FunctionDispatcher ae = (FunctionDispatcher) super.cloneFor(thread);
    ae.args = this.args.copy();
    if (evaluatedArgs != null) {
      ae.evaluatedArgs = new LinkedList(this.evaluatedArgs);
    }
    return ae;
  }
}
