package rainbow.functions.maths;

import rainbow.functions.Builtin;
import rainbow.types.ArcObject;
import rainbow.types.Pair;
import rainbow.types.ArcNumber;
import rainbow.types.Real;

public class Tangent extends Builtin {
  public Tangent() {
    super("tan");
  }

  protected ArcObject invoke(Pair args) {
    checkMaxArgCount(args, getClass(), 1);
    double result = Math.tan(ArcNumber.cast(args.car(), this).toDouble());
    return new Real(result);
  }
}
