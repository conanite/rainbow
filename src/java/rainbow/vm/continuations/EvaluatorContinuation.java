package rainbow.vm.continuations;

import rainbow.vm.Continuation;
import rainbow.vm.ArcThread;
import static rainbow.vm.compiler.Compiler.compile;
import rainbow.LexicalClosure;
import rainbow.types.ArcObject;

import java.util.Map;

public class EvaluatorContinuation extends ContinuationSupport {
  public static void compileAndEval(ArcThread thread, LexicalClosure lc, Continuation caller, ArcObject expression) {
    compile(thread, lc, new EvaluatorContinuation(thread, lc, caller), expression, new Map[0]);
  }

  public EvaluatorContinuation(ArcThread thread, LexicalClosure lc, Continuation caller) {
    super(thread, lc, caller);
  }

  public void onReceive(ArcObject o) {
    o.interpret(thread, lc, caller);
  }
}
