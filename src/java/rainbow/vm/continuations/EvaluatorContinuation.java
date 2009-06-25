package rainbow.vm.continuations;

import rainbow.vm.Continuation;
import static rainbow.vm.compiler.Compiler.compile;
import rainbow.LexicalClosure;
import rainbow.types.ArcObject;

import java.util.Map;

public class EvaluatorContinuation extends ContinuationSupport {
  public static void compileAndEval(LexicalClosure lc, Continuation caller, ArcObject expression) {
    compile(lc, new EvaluatorContinuation(lc, caller), expression, new Map[0]);
  }

  public EvaluatorContinuation(LexicalClosure lc, Continuation caller) {
    super(lc, caller);
  }

  public void onReceive(ArcObject o) {
    o.interpret(lc, caller);
  }
}
