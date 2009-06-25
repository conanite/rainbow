package rainbow.vm.interpreter;

import rainbow.types.ArcObject;
import rainbow.types.Symbol;
import rainbow.vm.Continuation;
import rainbow.vm.continuations.QuasiQuoteContinuation;
import rainbow.LexicalClosure;

public class QuasiQuotation extends ArcObject {
  private ArcObject target;

  public QuasiQuotation(ArcObject target) {
    this.target = target;
  }

  public ArcObject type() {
    return Symbol.make("quasiquotation");
  }

  public void interpret(LexicalClosure lc, Continuation caller) {
    new QuasiQuoteContinuation(lc, caller, target);
  }

  public String toString() {
    return "`" + target;
  }
}
