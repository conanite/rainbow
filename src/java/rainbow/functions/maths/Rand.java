package rainbow.functions.maths;

import rainbow.functions.Builtin;
import rainbow.types.*;
import rainbow.ArcError;
import rainbow.Nil;

public class Rand extends Builtin {
  public Rand() {
    super("rand");
  }

  public ArcObject invoke(Pair args) {
    if (args instanceof Nil) {
      return new Real(Maths.random.nextDouble());
    } else {
      ArcNumber r = (ArcNumber) args.car();
      if (!r.isInteger()) {
        throw new ArcError("rand: requires one exact integer argument, got " + args);
      }
      return new Rational(Math.abs(Maths.random.nextLong() % r.toInt()));
    }
  }
}
