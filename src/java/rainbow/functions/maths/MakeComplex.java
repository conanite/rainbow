package rainbow.functions.maths;

import rainbow.functions.Builtin;
import rainbow.types.ArcObject;
import rainbow.types.Pair;
import rainbow.types.ArcNumber;
import rainbow.types.Complex;

public class MakeComplex extends Builtin {
  public MakeComplex() {
    super("make-complex");
  }

  // todo this can be implemented in arc (def make-complex (real imag) (+ real (* 0+i imag)))
  protected ArcObject invoke(Pair args) {
    checkMaxArgCount(args, getClass(), 2);
    ArcNumber a = ArcNumber.cast(args.car(), this);
    ArcNumber b = ArcNumber.cast(args.cdr().car(), this);
    return new Complex(a, b);
  }
}
