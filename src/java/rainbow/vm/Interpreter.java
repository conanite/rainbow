package rainbow.vm;

import rainbow.ArcError;
import rainbow.InterpretationError;
import rainbow.LexicalClosure;
import rainbow.types.ArcObject;
import static rainbow.vm.compiler.Compiler.compile;
import rainbow.vm.continuations.EvaluatorContinuation;

import java.util.Map;

public final class Interpreter {
  public static void compileAndEval(ArcThread thread, LexicalClosure lc, Continuation caller, ArcObject expression) {
    compile(thread, lc, new EvaluatorContinuation(thread, lc, caller), expression, new Map[0]);
  }

  public static void interpret(ArcThread thread, LexicalClosure lc, Continuation caller, ArcObject expression) {
    try {
      expression.interpret(thread, lc, caller);
    } catch (ArcError ae) {
      caller.error(ae);
    } catch (Throwable t) {
      caller.error(new InterpretationError(expression, t));
    }
  }
}


