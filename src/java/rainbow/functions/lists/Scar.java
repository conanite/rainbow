package rainbow.functions.lists;

import rainbow.functions.Builtin;
import rainbow.types.ArcObject;
import rainbow.types.Pair;

public class Scar extends Builtin {
  public Scar() {
    super("scar");
  }

  public ArcObject invoke(Pair args) {
    return args.car().scar(args.cdr().car());
  }
}
