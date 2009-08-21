package rainbow.functions.lists;

import rainbow.functions.Builtin;
import rainbow.types.ArcObject;
import rainbow.types.Pair;
import rainbow.vm.VM;

public class Cons extends Builtin {
  public Cons() {
    super("cons");
  }

  public void invokef(VM vm, ArcObject arg1, ArcObject arg2) {
    vm.pushA(new Pair(arg1, arg2));
  }

  public ArcObject invoke(Pair args) {
    checkExactArgsCount(args, 2, getClass());
    return new Pair(args.car(), args.cdr().car());
  }
}
