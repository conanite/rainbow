package rainbow.functions.lists;

import rainbow.functions.Builtin;
import rainbow.types.ArcObject;
import rainbow.types.Pair;
import rainbow.vm.VM;

public class Scar extends Builtin {
  public Scar() {
    super("scar");
  }

  public void invokef(VM vm, ArcObject arg1, ArcObject arg2) {
    vm.pushA(arg1.scar(arg2));
  }

  public ArcObject invoke(Pair args) {
    return args.car().scar(args.cdr().car());
  }
}
