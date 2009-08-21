package rainbow.functions.threads;

import rainbow.functions.Builtin;
import rainbow.types.ArcObject;
import rainbow.types.Pair;
import rainbow.types.ArcThreadLocal;

public class ThreadLocalGet extends Builtin {
  public ThreadLocalGet() {
    super("thread-local-ref");
  }

  public ArcObject invoke(Pair args) {
    ArcThreadLocal tl = ArcThreadLocal.cast(args.car(), this);
    return tl.get();
  }
}
