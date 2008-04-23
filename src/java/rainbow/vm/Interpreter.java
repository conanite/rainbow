package rainbow.vm;

import rainbow.ArcError;
import rainbow.Bindings;
import rainbow.Function;
import rainbow.InterpretationError;
import rainbow.functions.Evaluation;
import rainbow.functions.InterpretedFunction;
import rainbow.functions.Threads;
import rainbow.types.ArcObject;
import rainbow.types.Pair;
import rainbow.vm.continuations.Compiler;
import rainbow.vm.continuations.EvaluatorContinuation;
import rainbow.vm.continuations.FunctionDispatcher;

public class Interpreter {
  public static boolean debug = false;
  private Continuation whatToDo;
  private Bindings namespace;
  private ArcObject expression;

  public static void interpret(ArcThread thread, Bindings namespace, Continuation whatToDo, ArcObject expression) {
    try {
      if (expression.isNil()) {
        whatToDo.eat(expression);
      } else if (Evaluation.isSpecialSyntax(expression)) {
        interpret(thread, namespace, whatToDo, Evaluation.ssExpand(expression));
      } else if (expression instanceof Pair) {
//        callFunctionRecursively(thread, namespace, whatToDo, expression);
        callFunction(thread, namespace, whatToDo, expression);
      } else if (expression instanceof InterpretedFunction) {
        whatToDo.eat(new Threads.Closure((Function) expression, namespace));
      } else {
        whatToDo.eat(expression.eval(namespace));
      }
    } catch (ArcError ae) {
      whatToDo.error(ae);
    } catch (Throwable t) {
      whatToDo.error(new InterpretationError(expression, t));
    }
  }

  private static void callFunctionRecursively(ArcThread thread, Bindings namespace, Continuation whatToDo, ArcObject expression) {
    interpret(thread, namespace, createDispacther(thread, namespace, whatToDo, expression), expression.car());
  }

  private static void callFunction(ArcThread thread, Bindings namespace, Continuation whatToDo, ArcObject invocation) {
    Interpreter functionCaller = new Interpreter(
            invocation.car(),
            namespace,
            createDispacther(thread, namespace, whatToDo, invocation));
    thread.continueWith(functionCaller);
  }

  private static FunctionDispatcher createDispacther(ArcThread thread, Bindings namespace, Continuation whatToDo, ArcObject invocation) {
    return new FunctionDispatcher(thread, namespace, whatToDo, invocation.cdr(), invocation.car());
  }

  public Interpreter(ArcObject expression, Bindings namespace, Continuation whatToDo) {
    this.expression = expression;
    this.namespace = namespace;
    this.whatToDo = whatToDo;
  }

  public void process(ArcThread thread) {
    interpret(thread, namespace, whatToDo, expression);
  }

  public String toString() {
    return getClass().getSimpleName() + ": " + expression + " : " + whatToDo;
  }

  public static void compileAndEval(ArcThread thread, Bindings namespace, Continuation whatToDo, ArcObject expression) {
    EvaluatorContinuation ec = new EvaluatorContinuation(thread, namespace, whatToDo);
    Compiler.compile(thread, namespace, ec, expression);
  }
}


