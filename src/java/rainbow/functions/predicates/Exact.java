package rainbow.functions.predicates;

import rainbow.functions.Builtin;
import rainbow.types.ArcObject;
import rainbow.types.Pair;
import rainbow.types.ArcNumber;
import rainbow.types.Rational;
import rainbow.Truth;

public class Exact extends Builtin {
  public Exact() {
    super("exact");
  }

  public ArcObject invoke(Pair args) {
    ArcNumber arg = ArcNumber.cast(args.car(), this);
    return Truth.valueOf(arg instanceof Rational && arg.isInteger());
  }
}
