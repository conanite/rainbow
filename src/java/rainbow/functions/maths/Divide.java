package rainbow.functions.maths;

import rainbow.functions.Builtin;
import rainbow.types.ArcObject;
import rainbow.types.Pair;
import rainbow.Nil;
import rainbow.ArcError;

public class Divide extends Builtin {
  public Divide() {
    super("/");
  }

  public ArcObject invoke(Pair args) {
    if (args instanceof Nil) {
      throw new ArcError("Function `-` expected at least 1 arg");
    }

    return Maths.precision(args).divide(args);
  }
}
