package rainbow.functions;

import rainbow.types.ArcObject;
import rainbow.types.Pair;
import rainbow.types.Symbol;

public class Uniq extends Builtin {
  private static long count = 0;

  public ArcObject invoke(Pair args) {
    checkMaxArgCount(args, getClass(), 1);
    synchronized (getClass()) {
      return Symbol.make("gs" + (++count));
    }
  }
}
