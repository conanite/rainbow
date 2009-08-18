package rainbow.functions.strings;

import rainbow.functions.Builtin;
import rainbow.types.ArcObject;
import rainbow.types.Pair;
import rainbow.types.StringOutputPort;

public class Inside extends Builtin {
  public Inside() {
    super("inside");
  }

  public ArcObject invoke(Pair args) {
    StringOutputPort sop = StringOutputPort.cast(args.car(), this);
    return sop.value();
  }
}
