package rainbow.functions.tables;

import rainbow.functions.Builtin;
import rainbow.types.ArcObject;
import rainbow.types.Pair;

public class Sref extends Builtin {
  public Sref() {
    super("sref");
  }

  public ArcObject invoke(Pair args) {
    return args.car().sref((Pair) args.cdr());
  }
}
