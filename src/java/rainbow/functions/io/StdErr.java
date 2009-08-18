package rainbow.functions.io;

import rainbow.functions.Builtin;
import rainbow.functions.IO;
import rainbow.types.ArcObject;
import rainbow.types.Pair;

public class StdErr extends Builtin {
  public StdErr() {
    super("stderr");
  }

  public ArcObject invoke(Pair args) {
    return IO.STD_ERR;
  }
}
