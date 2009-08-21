package rainbow.functions.maths;

import rainbow.functions.Builtin;
import rainbow.types.ArcObject;
import rainbow.types.Pair;
import rainbow.types.Complex;

public class ComplexParts extends Builtin {
  public ComplexParts() {
    super("complex-parts");
  }

  protected ArcObject invoke(Pair args) {
    checkMaxArgCount(args, getClass(), 1);
    Complex x = Complex.cast(args.car(), this);
    return Pair.buildFrom(x.realPart(), x.imaginaryPart());
  }
}
