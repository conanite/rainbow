package rainbow.functions.lists;

import rainbow.functions.Builtin;
import rainbow.types.ArcObject;
import rainbow.types.Pair;
import rainbow.vm.VM;

public class Car extends Builtin {
  public Car() {
    super("car");
  }

  public void invokef(VM vm, ArcObject arg) {
    vm.pushA(arg.car());
  }

  public ArcObject invoke(Pair args) {
    checkExactArgsCount(args, 1, getClass());
    return args.car().car();
  }
}
