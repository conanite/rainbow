package rainbow.functions.threads;

import rainbow.ArcError;
import rainbow.functions.Builtin;
import rainbow.types.ArcNumber;
import rainbow.types.Pair;
import rainbow.vm.VM;

public class Sleep extends Builtin {
  public Sleep() {
    super("sleep");
  }

  public void invoke(VM vm, Pair args) {
    ArcNumber seconds = ArcNumber.cast(args.car(), this);
    try {
      Thread.sleep((long) (seconds.toDouble() * 1000));
    } catch (InterruptedException e) {
      throw new ArcError("sleep: thread interruped : " + e.getMessage(), e);
    }
    vm.pushA(NIL);
  }
}
