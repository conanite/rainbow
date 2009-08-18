package rainbow.functions.io;

import rainbow.functions.Builtin;
import rainbow.types.ArcObject;
import rainbow.types.Pair;
import rainbow.types.Input;

public class Sread extends Builtin {
  public Sread() {
    super("sread");
  }

  public ArcObject invoke(Pair args) {
    return Input.cast(args.car(), this).readObject(args.cdr().car());
  }
}
