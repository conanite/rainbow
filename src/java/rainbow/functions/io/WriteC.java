package rainbow.functions.io;

import rainbow.functions.Builtin;
import static rainbow.functions.IO.chooseOutputPort;
import rainbow.types.ArcCharacter;
import rainbow.types.ArcObject;
import rainbow.types.Pair;

public class WriteC extends Builtin {
  public WriteC() {
    super("writec");
  }

  public ArcObject invoke(Pair args) {
    chooseOutputPort(args.cdr().car(), this).writeChar(ArcCharacter.cast(args.car(), this));
    return args.car();
  }
}
