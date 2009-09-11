package rainbow.functions;

import rainbow.LexicalClosure;
import rainbow.functions.interpreted.InterpretedFunction;
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

  public void invokef(VM vm) {
    expression.invokeN(vm, lc);
  }

  public void invokef(VM vm, ArcObject arg) {
    expression.invokeN(vm, lc, arg);
  }

  public void invokef(VM vm, ArcObject arg1, ArcObject arg2) {
    expression.invokeN(vm, lc, arg1, arg2);
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

  public String profileName() {
    return expression.profileName();
  }

  public ArcObject fn() {
    return expression;
  }
}
