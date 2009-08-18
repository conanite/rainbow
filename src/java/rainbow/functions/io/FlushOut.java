package rainbow.functions.io;

import rainbow.functions.Builtin;
import rainbow.functions.IO;
import rainbow.types.ArcObject;
import rainbow.types.Pair;

public class FlushOut extends Builtin {
  public FlushOut() {
    super("flushout");
  }

  public ArcObject invoke(Pair args) {
    IO.stdOut().flush();
    return T;
  }
}
