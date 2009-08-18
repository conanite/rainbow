package rainbow.functions.lists;

import rainbow.functions.Builtin;
import rainbow.types.ArcObject;
import rainbow.types.Pair;

public class Scdr extends Builtin {
  public Scdr() {
    super("scdr");
  }

  public ArcObject invoke(Pair args) {
    Pair target = (Pair) args.car();
    ArcObject newValue = args.cdr().car();
    target.setCdr(newValue);
    return newValue;
  }
}
