package rainbow.vm;

import rainbow.*;
import rainbow.functions.InterpretedFunction;
import rainbow.functions.Threads;
import rainbow.types.ArcObject;
import rainbow.types.Pair;
import rainbow.vm.continuations.Compiler;
import rainbow.vm.continuations.EvaluatorContinuation;
import rainbow.vm.continuations.FunctionDispatcher;

import java.util.Map;

public class Interpreter {
  public static void compileAndEval(ArcThread thread, LexicalClosure lc, Continuation caller, ArcObject expression) {
    Compiler.compile(thread, lc, new EvaluatorContinuation(thread, lc, caller), expression, new Map[0]);
  }

  public static void interpret(ArcThread thread, LexicalClosure lc, Continuation caller, ArcObject expression) {
    try {
      if (expression.isNil()) {
        caller.receive(expression);
      } else if (expression instanceof Pair) {
        callFunction(thread, lc, caller, expression);
      } else if (expression instanceof InterpretedFunction) {
        caller.receive(new Threads.Closure((Function) expression, lc));
      } else if (expression instanceof BoundSymbol) {
        caller.receive(((BoundSymbol)expression).lookup(lc));
      } else {
        caller.receive(expression.eval(thread.environment()));
      }
    } catch (ArcError ae) {
      caller.error(ae);
    } catch (Throwable t) {
      caller.error(new InterpretationError(expression, t));
    }
  }

  private static void callFunction(ArcThread thread, LexicalClosure lc, Continuation caller, ArcObject invocation) {
    thread.continueWith(new FunctionDispatcher(thread, lc, caller, invocation));
  }
}


