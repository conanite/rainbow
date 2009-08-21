package rainbow.functions.lists;

import rainbow.functions.Builtin;
import rainbow.types.*;
import rainbow.Nil;

public class NewString extends Builtin {
  public NewString() {
    super("newstring");
  }

  public ArcObject invoke(Pair args) {
    ArcNumber n = (ArcNumber) args.car();
    ArcCharacter c = ArcCharacter.NULL;
    if (!(args.cdr() instanceof Nil)) {
      c = (ArcCharacter) args.cdr().car();
    }
    StringBuilder b = new StringBuilder();
    for (int i = 0; i < n.toInt(); i++) {
      b.append(c.value());
    }
    return ArcString.make(b.toString());
  }
}
