package rainbow.functions.maths;

import rainbow.functions.Builtin;
import rainbow.types.ArcObject;
import rainbow.types.Pair;
import rainbow.types.Rational;
import rainbow.ArcError;

public class Quotient extends Builtin {
  public Quotient() {
    super("quotient");
  }

  public ArcObject invoke(Pair args) {
    checkExactArgsCount(args, 2, getClass());
    Rational top = Rational.cast(args.car(), this);
    Rational bottom = Rational.cast(args.cdr().car(), this);
    if (!(top.isInteger() && bottom.isInteger())) {
      throw new ArcError("Type error: " + this + " : expected integer, got " + args);
    }
    return Rational.make(top.toInt() / bottom.toInt());
  }
}
