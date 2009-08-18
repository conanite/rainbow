package rainbow.functions.system;

import rainbow.functions.Builtin;
import rainbow.types.ArcObject;
import rainbow.types.ArcString;
import rainbow.types.Pair;

public class PipeFrom extends Builtin {
  public PipeFrom() {
    super("pipe-from");
  }

  public ArcObject invoke(Pair args) {
    return SystemFunctions.pipeFrom(ArcString.cast(args.car(), this));
  }
}
