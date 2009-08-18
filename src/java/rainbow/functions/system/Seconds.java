package rainbow.functions.system;

import rainbow.functions.Builtin;
import rainbow.types.ArcObject;
import rainbow.types.Pair;
import rainbow.types.Rational;

public class Seconds extends Builtin {
  public Seconds() {
    super("seconds");
  }

  public ArcObject invoke(Pair args) {
    return Rational.make(System.currentTimeMillis() / 1000);
  }
}
