package rainbow.functions.io;

import rainbow.functions.Builtin;
import rainbow.functions.IO;
import rainbow.types.ArcObject;
import rainbow.types.Pair;

public class StdOut extends Builtin {
  public StdOut() {
    super("stdout");
  }

  public ArcObject invoke(Pair args) {
    return IO.stdOut();
  }
}
