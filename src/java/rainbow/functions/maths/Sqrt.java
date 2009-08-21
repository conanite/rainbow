package rainbow.functions.maths;

import rainbow.functions.Builtin;
import rainbow.types.*;

public class Sqrt extends Builtin {
  public Sqrt() {
    super("sqrt");
  }

  public ArcObject invoke(Pair args) {
    checkMaxArgCount(args, getClass(), 1);
    double result = Math.sqrt(ArcNumber.cast(args.car(), this).toDouble());
    if ((long)result == result) {
      return Rational.make((long) result);
    } else {
      return new Real(result);
    }
  }
}
