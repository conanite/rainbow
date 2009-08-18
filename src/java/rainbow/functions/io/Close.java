package rainbow.functions.io;

import rainbow.functions.Builtin;
import rainbow.functions.IO;
import rainbow.types.ArcObject;
import rainbow.types.Pair;

public class Close extends Builtin {
  public Close() {
    super("close");
  }

  public ArcObject invoke(Pair args) {
    while (!args.isNil()) {
      IO.close(args.car());
      args = (Pair) args.cdr();
    }
    return NIL;
  }
}
