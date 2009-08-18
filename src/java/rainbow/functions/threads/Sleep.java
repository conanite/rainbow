package rainbow.functions.threads;

import rainbow.functions.Builtin;
import rainbow.types.ArcObject;
import rainbow.types.Pair;
import rainbow.types.ArcNumber;
import rainbow.ArcError;

public class Sleep extends Builtin {
  public Sleep() {
    super("sleep");
  }

  public ArcObject invoke(Pair args) {
    ArcNumber seconds = ArcNumber.cast(args.car(), this);
    try {
      Thread.sleep((long) (seconds.toDouble() * 1000));
    } catch (InterruptedException e) {
      throw new ArcError("sleep: thread interruped : " + e.getMessage(), e);
    }
    return NIL;
  }
}
