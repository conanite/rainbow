package rainbow.functions.java;

import rainbow.functions.Builtin;
import rainbow.types.ArcObject;
import rainbow.types.Pair;
import rainbow.types.JavaObject;
import rainbow.types.Symbol;

public class JavaInvoke extends Builtin {
  public JavaInvoke() {
    super("java-invoke");
  }

  protected ArcObject invoke(Pair args) {
    JavaObject target = JavaObject.cast(args.car(), this);
    String methodName = Symbol.cast(args.cdr().car(), this).name();
    return JavaObject.wrap(target.invoke(methodName, (Pair) args.cdr().cdr().car()));
  }
}
