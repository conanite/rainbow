package rainbow.functions.io;

import rainbow.functions.Builtin;
import rainbow.functions.IO;
import rainbow.types.ArcObject;
import rainbow.types.Pair;

public class Write extends Builtin {
  public Write() {
    super("write");
  }

  public ArcObject invoke(Pair args) {
    IO.chooseOutputPort(args.cdr().car(), this).write(args.car());
    return NIL;
  }
}
