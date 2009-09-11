package rainbow.functions.typing;

import rainbow.functions.Builtin;
import rainbow.types.ArcObject;
import rainbow.types.Pair;
import rainbow.vm.VM;

public class Type extends Builtin {
  public Type() {
    super("type");
  }

  public void invokef(VM vm, ArcObject arg) {
    vm.pushA(arg.type());
  }

  public ArcObject invoke(Pair args) {
    checkMaxArgCount(args, getClass(), 1);
    ArcObject arg = args.car();
    return arg.type();
  }
}
