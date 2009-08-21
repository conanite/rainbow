package rainbow.functions.threads;

import rainbow.functions.Builtin;
import rainbow.types.ArcObject;
import rainbow.types.Pair;
import rainbow.types.ArcThreadLocal;

public class ThreadLocalSet extends Builtin {
  public ThreadLocalSet() {
    super("thread-local-set");
  }

  public ArcObject invoke(Pair args) {
    ArcThreadLocal tl = ArcThreadLocal.cast(args.car(), this);
    ArcObject value = args.cdr().car();
    tl.set(value);
    return value;
  }
}
