package rainbow.functions.strings;

import rainbow.functions.Builtin;
import rainbow.types.ArcObject;
import rainbow.types.Pair;
import rainbow.types.StringInputPort;
import rainbow.types.ArcString;

public class InString extends Builtin {
  public InString() {
    super("instring");
  }

  public ArcObject invoke(Pair args) {
    return new StringInputPort(ArcString.cast(args.car(), this).value());
  }
}
