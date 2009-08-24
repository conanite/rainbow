package rainbow.functions.maths;

import rainbow.functions.Builtin;
import rainbow.types.ArcObject;
import rainbow.types.Pair;
import rainbow.types.ArcNumber;
import rainbow.types.Real;

public class Acos extends Builtin {
  public Acos() {
    super("acos");
  }

  protected ArcObject invoke(Pair args) {
    checkMaxArgCount(args, getClass(), 1);
    double result = Math.acos(ArcNumber.cast(args.car(), this).toDouble());
    return new Real(result);
  }
}
