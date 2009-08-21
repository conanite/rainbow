package rainbow.functions.maths;

import rainbow.functions.Builtin;
import rainbow.types.ArcObject;
import rainbow.types.Pair;
import rainbow.types.ArcNumber;
import rainbow.types.Rational;

public class Trunc extends Builtin {
  public Trunc() {
    super("trunc");
  }

  public ArcObject invoke(Pair args) {
    checkMaxArgCount(args, getClass(), 1);
    double value = ((ArcNumber) args.car()).toDouble();
    return new Rational((long) Math.floor(value));
  }
}
