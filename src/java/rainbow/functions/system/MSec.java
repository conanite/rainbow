package rainbow.functions.system;

import rainbow.functions.Builtin;
import rainbow.types.ArcObject;
import rainbow.types.Pair;
import rainbow.types.Rational;

public class MSec extends Builtin {
  public MSec() {
    super("msec");
  }

  public ArcObject invoke(Pair args) {
    return Rational.make(System.currentTimeMillis());
  }
}
