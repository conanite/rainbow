package rainbow.functions.system;

import rainbow.functions.Builtin;
import rainbow.types.ArcObject;
import rainbow.types.Pair;

public class SetUID extends Builtin {
  public SetUID() {
    super("setuid");
  }

  protected ArcObject invoke(Pair args) {
    System.err.println("setuid:not implemented");
    return NIL;
  }
}
