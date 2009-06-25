package rainbow.vm.continuations;

import rainbow.LexicalClosure;
import rainbow.functions.InterpretedFunction;
import rainbow.types.ArcObject;
import rainbow.vm.Continuation;

public class FunctionEvaluator extends ContinuationSupport {
  private InterpretedFunction f;
  private int index;

  public FunctionEvaluator(LexicalClosure lc, Continuation caller, InterpretedFunction f) {
    super(lc, caller);
    this.f = f;
  }

  public void onReceive(ArcObject o) {
    ArcObject expression = f.nth(index++);
    Continuation caller1 = f.last(index) ? caller : this;
    expression.interpret(lc, caller1);
  }

  protected ArcObject getCurrentTarget() {
    return index == 0 ? ArcObject.NIL : f.nth(index - 1);
  }

  public static void evaluate(LexicalClosure lc, Continuation caller, InterpretedFunction f) {
    switch (f.length()) {
      case 0:
        caller.receive(ArcObject.NIL);
        break;
      case 1:
        f.nth(0).interpret(lc, caller);
        break;
      default:
        new FunctionEvaluator(lc, caller, f).receive(ArcObject.NIL);
    }
  }
}
