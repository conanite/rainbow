package rainbow.functions.system;

import rainbow.functions.Builtin;
import rainbow.types.ArcObject;
import rainbow.types.Pair;

public class Quit extends Builtin {
  public Quit() {
    super("quit");
  }

  public ArcObject invoke(Pair args) {
    System.exit(0);
    return null;
  }
}
