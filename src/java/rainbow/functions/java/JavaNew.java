package rainbow.functions.java;

import rainbow.functions.Builtin;
import rainbow.types.*;

public class JavaNew extends Builtin {
  public JavaNew() {
    super("java-new");
  }

  protected ArcObject invoke(Pair args) {
    Pair constructArgs = (Pair) args.cdr();
    String name = null;
    Pair types = null;
    if (args.car() instanceof ArcString) {
      name = ((ArcString) args.car()).value();
    } else if (args.car() instanceof Symbol) {
      name = ((Symbol) args.car()).name();
    } else {
      Pair nameTypes = Pair.cast(args.car(), this);
      types = (Pair) nameTypes.cdr();
      if (nameTypes.car() instanceof ArcString) {
        name = ((ArcString) nameTypes.car()).value();
      } else if (nameTypes.car() instanceof Symbol) {
        name = ((Symbol) nameTypes.car()).name();
      }
    }
    return JavaObject.instantiate(name, types, constructArgs);
  }
}
