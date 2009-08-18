package rainbow.functions.io;

import rainbow.functions.Builtin;
import rainbow.functions.IO;
import rainbow.types.ArcObject;
import rainbow.types.Pair;

public class ReadC extends Builtin {
  public ReadC() {
    super("readc");
  }

  public ArcObject invoke(Pair args) {
    return IO.chooseInputPort(args.car(), this).readCharacter();
  }
}
