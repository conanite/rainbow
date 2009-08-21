package rainbow.functions.io;

import rainbow.functions.Builtin;
import rainbow.functions.IO;
import rainbow.types.ArcObject;
import rainbow.types.Pair;

public class ForceClose extends Builtin {
  public ForceClose() {
    super("force-close");
  }

  public ArcObject invoke(Pair args) {
    return IO.closeAll(args);
  }
}
