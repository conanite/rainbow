package rainbow.functions.system;

import rainbow.functions.Builtin;
import rainbow.types.ArcObject;
import rainbow.types.Pair;
import rainbow.types.Rational;

public class CurrentGcMilliseconds extends Builtin {
  public CurrentGcMilliseconds() {
    super("current-gc-milliseconds");
  }

  public ArcObject invoke(Pair args) {
    return Rational.make(1);
  }
}
