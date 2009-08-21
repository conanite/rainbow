package rainbow.functions.predicates;

import rainbow.functions.Builtin;
import rainbow.types.ArcObject;
import rainbow.types.Pair;
import rainbow.Nil;

public class GreaterThan extends Builtin {
  public GreaterThan() {
    super(">");
  }

  public ArcObject invoke(Pair args) {
    ArcObject left = args.car();
    Pair others = (Pair) args.cdr();
    while (!(others instanceof Nil)) {
      ArcObject right = others.car();
      int comparison = left.compareTo(right);
      if (comparison <= 0) {
        return NIL;
      }
      left = right;
      others = (Pair) others.cdr();
    }
    return T;
  }
}
