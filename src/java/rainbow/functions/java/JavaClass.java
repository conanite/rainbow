package rainbow.functions.java;

import rainbow.functions.Builtin;
import rainbow.types.ArcObject;
import rainbow.types.Pair;
import rainbow.types.ArcString;
import rainbow.types.JavaObject;

public class JavaClass extends Builtin {
  public JavaClass() {
    super("java-class");
  }

  protected ArcObject invoke(Pair args) {
    String className = ArcString.cast(args.car(), this).value();
    return JavaObject.getClassInstance(className);
  }
}
