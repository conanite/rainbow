package rainbow.functions.maths;

import rainbow.functions.Builtin;
import rainbow.types.ArcObject;
import rainbow.types.Pair;
import rainbow.types.ArcNumber;
import rainbow.types.Real;

public class Asin extends Builtin {
  public Asin() {
    super("asin");
  }

  protected ArcObject invoke(Pair args) {
    checkMaxArgCount(args, getClass(), 1);
    double result = Math.asin(ArcNumber.cast(args.car(), this).toDouble());
    return new Real(result);
  }
}
