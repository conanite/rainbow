package rainbow.functions.typing;

import rainbow.functions.Builtin;
import rainbow.types.ArcObject;
import rainbow.types.Pair;
import rainbow.types.Tagged;

public class Rep extends Builtin {
  public Rep() {
    super("rep");
  }

  public ArcObject invoke(Pair args) {
    return Tagged.cast(args.car(), this).getRep();
  }
}
