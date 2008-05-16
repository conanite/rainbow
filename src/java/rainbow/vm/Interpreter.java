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
  public static void compileAndEval(ArcThread thread, LexicalClosure lc, Continuation whatToDo, ArcObject expression) {
    Compiler.compile(thread, lc, new EvaluatorContinuation(thread, lc, whatToDo), expression, new Map[0]);
  }

  public static void interpret(ArcThread thread, LexicalClosure lc, Continuation whatToDo, ArcObject expression) {
    try {
      if (expression.isNil()) {
        whatToDo.receive(expression);
      } else if (expression instanceof Pair) {
        callFunction(thread, lc, whatToDo, expression);
      } else if (expression instanceof InterpretedFunction) {
        whatToDo.receive(new Threads.Closure((Function) expression, lc));
      } else if (expression instanceof BoundSymbol) {
        whatToDo.receive(((BoundSymbol)expression).lookup(lc));
      } else {
        whatToDo.receive(expression.eval(thread.environment()));
      }
    } catch (ArcError ae) {
      whatToDo.error(ae);
    } catch (Throwable t) {
      whatToDo.error(new InterpretationError(expression, t));
    }
  }

  private static void callFunction(ArcThread thread, LexicalClosure lc, Continuation whatToDo, ArcObject invocation) {
    thread.continueWith(new FunctionDispatcher(thread, lc, whatToDo, invocation));
  }
}


