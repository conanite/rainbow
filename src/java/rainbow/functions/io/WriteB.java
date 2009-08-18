package rainbow.functions.io;

import rainbow.functions.Builtin;
import rainbow.functions.IO;
import rainbow.types.ArcObject;
import rainbow.types.Pair;
import rainbow.types.Rational;

public class WriteB extends Builtin {
  public WriteB() {
    super("writeb");
  }

  public ArcObject invoke(Pair args) {
    IO.chooseOutputPort(args.cdr().car(), this).writeByte(Rational.cast(args.car(), this));
    return NIL;
  }
}
