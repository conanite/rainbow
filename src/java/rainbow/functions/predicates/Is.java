package rainbow.functions.predicates;

import rainbow.functions.Builtin;
import rainbow.types.ArcObject;
import rainbow.types.Pair;

public class Is extends Builtin {
  public Is() {
    super("is");
  }

  public ArcObject invoke(Pair args) {
    return checkIs(args.car(), args.cdr());
  }

  private ArcObject checkIs(ArcObject test, ArcObject args) {
    if (args.isNil()) {
      return T;
    }

    if (!test.isSame(args.car())) {
      return NIL;
    }

    return checkIs(test, args.cdr());
  }

}
