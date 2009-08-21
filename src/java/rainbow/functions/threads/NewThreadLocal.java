package rainbow.functions.threads;

import rainbow.functions.Builtin;
import rainbow.types.ArcObject;
import rainbow.types.Pair;
import rainbow.types.ArcThreadLocal;

public class NewThreadLocal extends Builtin {
  public NewThreadLocal() {
    super("thread-local");
  }

  public ArcObject invoke(Pair args) {
    return new ArcThreadLocal();
  }
}
