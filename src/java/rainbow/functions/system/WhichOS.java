package rainbow.functions.system;

import rainbow.functions.Builtin;
import rainbow.types.ArcObject;
import rainbow.types.Pair;
import rainbow.types.Symbol;

public class WhichOS extends Builtin {
  public WhichOS() {
    super("which-os");
  }

  public ArcObject invoke(Pair args)  {
    return Symbol.mkSym(System.getProperty("os.name").replaceAll(" ", "").toLowerCase());
  }
}
