package rainbow.functions.typing;

import rainbow.functions.Builtin;
import rainbow.types.ArcObject;
import rainbow.types.Pair;

public class Coerce extends Builtin {
  public Coerce() {
    super("coerce");
  }

  public ArcObject invoke(Pair args) {
    return Typing.coerce(args);
  }
}
