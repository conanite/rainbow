package rainbow.functions.maths;

import rainbow.functions.Builtin;
import rainbow.types.ArcObject;
import rainbow.types.Pair;

public class Multiply extends Builtin {
  public Multiply() {
    super("*");
  }

  public ArcObject invoke(Pair args) {
    return Maths.precision(args).multiply(args);
  }
}
