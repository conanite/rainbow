package rainbow.functions.errors;

import rainbow.functions.Builtin;
import rainbow.types.ArcObject;
import rainbow.types.Pair;
import rainbow.types.ArcString;
import rainbow.ArcError;

public class Err extends Builtin {
  public Err() {
    super("err");
  }

  public ArcObject invoke(Pair args) {
    throw new ArcError(ArcString.cast(args.car(), this).value());
  }
}
