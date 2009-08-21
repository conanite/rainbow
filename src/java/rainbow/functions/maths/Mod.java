package rainbow.functions.maths;

import rainbow.functions.Builtin;
import rainbow.types.ArcObject;
import rainbow.types.Pair;
import rainbow.types.ArcNumber;
import rainbow.types.Rational;
import rainbow.ArcError;

public class Mod extends Builtin {
  public Mod() {
    super("mod");
  }

  public ArcObject invoke(Pair args) {
    checkMaxArgCount(args, getClass(), 2);
    ArcNumber first = (ArcNumber) args.car();
    ArcNumber second = (ArcNumber) args.cdr().car();
    if (!first.isInteger() || !second.isInteger()) {
      throw new ArcError("modulo: expects integer, got " + args);
    }
    long numerator = first.toInt();
    long divisor = second.toInt();
    long result = numerator % divisor;
    if (result < 0) {
      result += divisor;
    }
    return Rational.make(result);
  }
}
