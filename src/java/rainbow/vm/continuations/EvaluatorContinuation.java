package rainbow.vm.continuations;

import rainbow.vm.Continuation;
import rainbow.vm.ArcThread;
import rainbow.vm.Interpreter;
import rainbow.LexicalClosure;
import rainbow.types.ArcObject;

public class EvaluatorContinuation extends ContinuationSupport {
  public EvaluatorContinuation(ArcThread thread, LexicalClosure lc, Continuation caller) {
    super(thread, lc, caller);
  }

  public void onReceive(ArcObject o) {
    Interpreter.interpret(thread, lc, caller, o);
  }
}
