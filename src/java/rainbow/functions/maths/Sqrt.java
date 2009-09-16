package rainbow.functions.maths;

import rainbow.functions.Builtin;
import rainbow.types.*;
import rainbow.vm.VM;

public class Sqrt extends Builtin {
  public Sqrt() {
    super("sqrt");
  }

  public void invokef(VM vm, ArcObject arg) {
    vm.pushA(arg.sqrt());
  }

  public void invoke(VM vm, Pair args) {
    checkExactArgsCount(args, 1, getClass());
    invokef(vm, args.car());
  }
}
