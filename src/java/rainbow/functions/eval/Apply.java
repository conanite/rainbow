package rainbow.functions.eval;

import rainbow.functions.Builtin;
import rainbow.vm.VM;
import rainbow.types.Pair;

public class Apply extends Builtin {
  public Apply() {
    super("apply");
  }

  public void invoke(VM vm, Pair args) {
    args.car().invoke(vm, constructApplyArgs((Pair) args.cdr()));
  }

  private Pair constructApplyArgs(Pair args) {
    if (args.cdr().isNil()) {
      return Pair.cast(args.car(), this);
    } else {
      return new Pair(args.car(), constructApplyArgs(Pair.cast(args.cdr(), this)));
    }
  }
}
