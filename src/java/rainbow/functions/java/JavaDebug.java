package rainbow.functions.java;

import rainbow.functions.Builtin;
import rainbow.types.ArcObject;
import rainbow.types.Pair;

public class JavaDebug extends Builtin {
  public JavaDebug() {
    super("java-debug");
  }

  protected ArcObject invoke(Pair args) {
    return args.car();
  }
}
