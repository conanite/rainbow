package rainbow.functions;

import rainbow.types.ArcObject;
import rainbow.types.Pair;
import rainbow.Function;
import rainbow.LexicalClosure;
import rainbow.vm.Continuation;

public class Closure extends ArcObject {
  private Function expression;
  private LexicalClosure lc;

  public Closure(Function expression, LexicalClosure lc) {
    this.expression = expression;
    this.lc = lc;
  }

  public void invoke(LexicalClosure lc, Continuation caller, Pair args) {
    expression.invoke(this.lc, caller, args);
  }

  public ArcObject type() {
    return Builtin.TYPE;
  }

  public String toString() {
    return expression.toString();
  }
}
