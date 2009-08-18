package rainbow.functions.predicates;

import rainbow.functions.Builtin;
import rainbow.types.ArcObject;
import rainbow.types.Pair;

public class LessThan extends Builtin {
  public LessThan() {
    super("<");
  }

  public ArcObject invoke(Pair args) {
    ArcObject left = args.car();
    Pair others = (Pair) args.cdr();
    while (!others.isNil()) {
      ArcObject right = others.car();
      int comparison = left.compareTo(right);
      if (comparison >= 0) {
        return NIL;
      }
      left = right;
      others = (Pair) others.cdr();
    }
    return T;
  }
}
