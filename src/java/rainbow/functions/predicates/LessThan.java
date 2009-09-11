package rainbow.functions.predicates;

import rainbow.functions.Builtin;
import rainbow.types.ArcObject;
import rainbow.types.Pair;
import rainbow.Nil;
import rainbow.vm.VM;

public class LessThan extends Builtin {
  public LessThan() {
    super("<");
  }

  public void invokef(VM vm) {
    vm.pushA(T);
  }

  public void invokef(VM vm, ArcObject arg1) {
    vm.pushA(T);
  }

  public void invokef(VM vm, ArcObject arg1, ArcObject arg2) {
    vm.pushA(arg1.compareTo(arg2) >= 0 ? NIL : T);
  }

  public ArcObject invoke(Pair args) {
    ArcObject left = args.car();
    Pair others = (Pair) args.cdr();
    while (!(others instanceof Nil)) {
      ArcObject right = others.car();
      int comparison = left.compareTo(right);
      if (comparison >= 0) {
        return NIL;
      }
      left = right;
      others = (Pair) others.cdr();
    }
    return T;
  }
}
