package rainbow.functions.io;

import rainbow.functions.Builtin;
import rainbow.functions.IO;
import rainbow.types.ArcObject;
import rainbow.types.Pair;

public class ReadB extends Builtin {
  public ReadB() {
    super("readb");
  }

  public ArcObject invoke(Pair args) {
    return IO.chooseInputPort(args.car(), this).readByte();
  }
}
