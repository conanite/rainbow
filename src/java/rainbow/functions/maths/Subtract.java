package rainbow.functions.maths;

import rainbow.functions.Builtin;
import rainbow.types.ArcObject;
import rainbow.types.Pair;
import rainbow.types.ArcNumber;
import rainbow.Nil;
import rainbow.ArcError;

public class Subtract extends Builtin {
  public Subtract() {
    super("-");
  }

  public ArcObject invoke(Pair args) {
    if (args instanceof Nil) {
      throw new ArcError("Function `-` expected at least 1 arg");
    }

    ArcNumber first = ((ArcNumber) args.car()).negate();
    Pair rest = (Pair) args.cdr();

    if (rest instanceof Nil) {
      return first;
    }
    return Add.sum(new Pair(first, rest)).negate();
  }
}
