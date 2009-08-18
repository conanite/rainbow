package rainbow.functions.lists;

import rainbow.functions.Builtin;
import rainbow.types.ArcObject;
import rainbow.types.Pair;
import rainbow.ArcError;

public class Cdr extends Builtin {
  public Cdr() {
    super("cdr");
  }

  public ArcObject invoke(Pair args) {
    try {
      args.cdr().mustBeNil();
    } catch (NotNil notNil) {
      throw new ArcError("cdr: expects only one argument: got " + args);
    }
    return args.car().cdr();
  }
}
