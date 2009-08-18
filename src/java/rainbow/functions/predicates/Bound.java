package rainbow.functions.predicates;

import rainbow.Truth;
import rainbow.functions.Builtin;
import rainbow.types.ArcObject;
import rainbow.types.Pair;
import rainbow.types.Symbol;

public class Bound extends Builtin {
  public Bound() {
    super("bound");
  }

  public ArcObject invoke(Pair args) {
    checkMaxArgCount(args, getClass(), 2);
    Symbol sym = Symbol.cast(args.car(), this);
    return Truth.valueOf(sym.bound());
  }
}
