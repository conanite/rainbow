package rainbow.functions.system;

import rainbow.functions.Builtin;
import rainbow.types.ArcObject;
import rainbow.types.Pair;

public class Declare extends Builtin {
  public Declare() {
    super("declare");
  }

  public ArcObject invoke(Pair args)  {
    return NIL;
  }
}
