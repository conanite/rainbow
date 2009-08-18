package rainbow.functions.errors;

import rainbow.functions.Builtin;
import rainbow.types.ArcObject;
import rainbow.types.Pair;
import rainbow.types.ArcException;

public class Details extends Builtin {
  public Details() {
    super("details");
  }

  public ArcObject invoke(Pair args) {
    return ArcException.cast(args.car(), this).message();
  }
}
