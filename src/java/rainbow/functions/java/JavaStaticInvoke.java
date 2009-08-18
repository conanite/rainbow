package rainbow.functions.java;

import rainbow.functions.Builtin;
import rainbow.types.*;

public class JavaStaticInvoke extends Builtin {
  public JavaStaticInvoke() {
    super("java-static-invoke");
  }

  protected ArcObject invoke(Pair args) {
    String target = ArcString.cast(args.car(), this).value();
    String methodName = Symbol.cast(args.cdr().car(), this).name();
    return JavaObject.wrap(JavaObject.staticInvoke(target, methodName, (Pair) args.cdr().cdr()));
  }
}
