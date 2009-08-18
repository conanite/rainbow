package rainbow.functions.java;

import rainbow.functions.Builtin;
import rainbow.types.ArcObject;
import rainbow.types.Pair;
import rainbow.Console;

public class JavaDebug extends Builtin {
  public JavaDebug() {
    super("java-debug");
  }

  protected ArcObject invoke(Pair args) {
    Console.debugJava = (args.car() == T);
    return args.car();
  }
}
