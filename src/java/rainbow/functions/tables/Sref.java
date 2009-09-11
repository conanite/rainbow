package rainbow.functions.tables;

import rainbow.functions.Builtin;
import rainbow.types.ArcObject;
import rainbow.types.Pair;
import rainbow.vm.VM;

public class Sref extends Builtin {
  public Sref() {
    super("sref");
  }

  public void invokef(VM vm, ArcObject arg1, ArcObject arg2, ArcObject arg3) {
    vm.pushA(arg1.sref(arg2, arg3));
  }

  public void invoke(VM vm, Pair args) {
    checkExactArgsCount(args, 3, getClass());
    invokef(vm, args.car(), args.cdr().car(), args.cdr().cdr().car());
  }
}
