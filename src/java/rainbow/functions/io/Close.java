package rainbow.functions.io;

import rainbow.functions.Builtin;
import rainbow.functions.IO;
import rainbow.types.ArcObject;
import rainbow.types.Pair;
import rainbow.Nil;

public class Close extends Builtin {
  public Close() {
    super("close");
  }

  public ArcObject invoke(Pair args) {
    while (!(args instanceof Nil)) {
      IO.close(args.car());
      args = (Pair) args.cdr();
    }
    return NIL;
  }
}
