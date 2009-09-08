package rainbow.functions.maths;

import rainbow.functions.Builtin;
import rainbow.types.ArcNumber;
import rainbow.types.ArcObject;
import rainbow.types.Pair;
import rainbow.vm.VM;

public class Mod extends Builtin {
  public Mod() {
    super("mod");
  }

  public void invokef(VM vm, ArcObject arg1, ArcObject arg2) {
    vm.pushA(((ArcNumber)arg1).mod((ArcNumber) arg2));
  }

  public void invoke(VM vm, Pair args) {
    checkMaxArgCount(args, getClass(), 2);
    invokef(vm, args.car(), args.cdr().car());
  }
}
