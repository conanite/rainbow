package rainbow.functions.strings;

import rainbow.functions.Builtin;
import rainbow.types.ArcObject;
import rainbow.types.Pair;
import rainbow.types.StringOutputPort;

public class OutString extends Builtin {
  public OutString() {
    super("outstring");
  }

  public ArcObject invoke(Pair args) {
    return new StringOutputPort();
  }
}
