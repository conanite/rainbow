package rainbow.functions.threads;

import rainbow.Truth;
import rainbow.functions.Builtin;
import rainbow.types.ArcObject;
import rainbow.types.Pair;
import rainbow.vm.VM;

public class Dead extends Builtin {
  public Dead() {
    super("dead");
  }

  public ArcObject invoke(Pair args) {
    VM victim = (VM) args.car();
    return Truth.valueOf(victim.dead());
  }
}
