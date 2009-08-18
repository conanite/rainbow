package rainbow.functions.system;

import rainbow.functions.Builtin;
import rainbow.types.ArcObject;
import rainbow.types.Pair;
import rainbow.types.Rational;

public class CurrentProcessMilliseconds extends Builtin {
  public CurrentProcessMilliseconds() {
    super("current-process-milliseconds");
  }

  public ArcObject invoke(Pair args) {
    return Rational.make(1);
  }
}
