package rainbow.functions.maths;

import rainbow.functions.Builtin;
import rainbow.types.ArcObject;
import rainbow.types.Pair;
import rainbow.types.Complex;
import rainbow.types.Real;

public class PolarCoordinates extends Builtin {
  public PolarCoordinates() {
    super("polar-coordinates");
  }

  protected ArcObject invoke(Pair args) {
    checkMaxArgCount(args, getClass(), 1);
    Complex x = Complex.cast(args.car(), this);
    return Pair.buildFrom(new Real(x.radius()), new Real(x.theta()));
  }
}
