package rainbow.functions.java;

import rainbow.functions.Builtin;
import rainbow.types.*;
import rainbow.ArcError;

public class JavaStaticField extends Builtin {
  public JavaStaticField() {
    super("java-static-field");
  }

  protected ArcObject invoke(Pair args) {
    try {
      String target = ArcString.cast(args.car(), this).value();
      String fieldName = Symbol.cast(args.cdr().car(), this).name();
      return JavaObject.wrap(JavaObject.getStaticFieldValue(target, fieldName));
    } catch (Throwable e) {
      throw new ArcError("could not static-invoke " + args, e);
    }
  }
}
