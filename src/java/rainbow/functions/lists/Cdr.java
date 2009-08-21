package rainbow.functions.lists;

import rainbow.functions.Builtin;
import rainbow.types.ArcObject;
import rainbow.types.Pair;
import rainbow.vm.VM;

public class Cdr extends Builtin {
  public Cdr() {
    super("cdr");
  }

  public void invokef(VM vm, ArcObject arg) {
    vm.pushA(arg.cdr());
  }

  public ArcObject invoke(Pair args) {
    checkExactArgsCount(args, 1, getClass());
    return args.car().cdr();
  }
}
