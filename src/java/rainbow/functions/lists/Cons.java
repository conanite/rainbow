package rainbow.functions.lists;

import rainbow.functions.Builtin;
import rainbow.types.ArcObject;
import rainbow.types.Pair;

public class Cons extends Builtin {
  public Cons() {
    super("cons");
  }

  public ArcObject invoke(Pair args) {
    checkExactArgsCount(args, 2, getClass());
    return new Pair(args.car(), args.cdr().car());
  }
}
