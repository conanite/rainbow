package rainbow.functions;

import rainbow.LexicalClosure;
import rainbow.types.ArcObject;
import rainbow.types.Pair;
import rainbow.vm.VM;

public class Closure extends ArcObject {
  private InterpretedFunction expression;
  private LexicalClosure lc;

  public Closure(InterpretedFunction expression, LexicalClosure lc) {
    this.expression = expression;
    this.lc = lc;
  }

  public void invoke(VM vm, Pair args) {
    expression.invoke(vm, lc, args);
  }

  public ArcObject type() {
    return Builtin.TYPE;
  }

  public String toString() {
    return expression.toString();
  }
}
