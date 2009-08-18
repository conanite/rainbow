package rainbow.vm.interpreter;

import rainbow.types.ArcObject;
import rainbow.types.Symbol;
import rainbow.vm.continuations.QuasiQuoteContinuation;

import java.util.List;

public class QuasiQuotation extends ArcObject {
  private ArcObject target;

  public QuasiQuotation(ArcObject target) {
    this.target = target;
  }

  public ArcObject type() {
    return Symbol.mkSym("quasiquotation");
  }

  public void addInstructions(List i) {
    QuasiQuoteContinuation.addInstructions(i, target);
  }

  public String toString() {
    return "`" + target;
  }
}
