package rainbow.vm.continuations;

import rainbow.vm.Continuation;
import rainbow.vm.ArcThread;
import rainbow.vm.Interpreter;
import rainbow.LexicalClosure;
import rainbow.functions.InterpretedFunction;
import rainbow.types.ArcObject;

public class FunctionEvaluator extends ContinuationSupport {
  private InterpretedFunction f;
  private int index;

  public FunctionEvaluator(ArcThread thread, LexicalClosure lc, Continuation caller, InterpretedFunction f) {
    super(thread, lc, caller);
    this.f = f;
    index = 0;
  }

  public void onReceive(ArcObject o) {
    ArcObject expression = f.nth(index++);
    Interpreter.interpret(thread, lc, f.last(index) ? caller : this, expression);
  }
}
