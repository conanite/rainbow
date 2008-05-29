package rainbow.vm.continuations;

import rainbow.*;
import rainbow.functions.Builtin;
import rainbow.functions.InterpretedFunction;
import rainbow.types.*;
import rainbow.vm.ArcThread;
import rainbow.vm.Continuation;
import rainbow.vm.Interpreter;

import javax.swing.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.awt.*;

public class FunctionDispatcher extends ContinuationSupport {
  public static final boolean ALLOW_MACRO_EXPANSION = false;
  public static final Symbol TYPE_DISPATCHER_TABLE = (Symbol) Symbol.make("call*");

  private static final Object EXPECT_FUNCTION = new Object();
  private static final Object EXPECT_ARGUMENT = new Object();

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
    } else if (Console.ARC2_COMPATIBILITY) {
      arc2CompatibleTypeDispatch(fn);
    } else if (Console.ANARKI_COMPATIBILITY) {
      anarkiCompatibleTypeDispatch(fn);
    } else {
      throw new ArcError("Expected a function : got " + fn + " with args " + args);
    }
  }

  private void anarkiCompatibleTypeDispatch(ArcObject fn) {
    Hash dispatchers = (Hash) thread.environment().lookup(TYPE_DISPATCHER_TABLE);
    try {
      ArcObject typeFunction = dispatchers.value(fn.type());
      if (typeFunction.isNil() || !(typeFunction instanceof Function)) {
        throw new ArcError("Function dispatch on inappropriate object: " + fn);
      }
      typeDispatch((Function) typeFunction, Tagged.rep(fn));
    } catch (NullPointerException e) {
      if (dispatchers == null) {
        throw new ArcError("call* table not found in environment: if you are not using anarki please specify --strict-arc on the command-line");
      } else {
        throw e;
      }
    }
  }

  private void arc2CompatibleTypeDispatch(ArcObject fn) {
    if (fn instanceof Pair) {
      typeDispatch(Pair.REF, fn);
    } else if (fn instanceof ArcString) {
      typeDispatch(ArcString.REF, fn);
    } else if (fn instanceof Hash) {
      typeDispatch(Hash.REF, fn);
    } else {
      throw new ArcError("Expected a function, cons, hash, or string : got " + fn + " with args " + args);
    }
  }

  private void typeDispatch(Function function, ArcObject target) {
    evaluatedArgs = new ArrayList();
    evaluatedArgs.add(target);
    evaluate(function);
  }

  private void evaluate(Function f) {
    if (args.isNil()) {
      f.invoke(thread, lc, caller, (Pair) args);
    } else {
      state = EXPECT_ARGUMENT;
      this.f = f;
      evaluatedArgs = evaluatedArgs == null ? new ArrayList(((Pair) args).size()) : evaluatedArgs;
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
      throw new Error("FD.digestArgument: we should never get in here, isn't this handled by Interpreter???");
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

  protected ArcObject getCurrentTarget() {
    return functionName;
  }
}
