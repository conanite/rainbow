package rainbow.functions.predicates;

import rainbow.Truth;
import rainbow.Nil;
import rainbow.functions.Builtin;
import rainbow.types.ArcObject;
import rainbow.types.Pair;
import rainbow.vm.VM;

public class Is extends Builtin {
  public Is() {
    super("is");
  }

  public void invokef(VM vm) {
    vm.pushA(T);
  }

  public void invokef(VM vm, ArcObject arg) {
    vm.pushA(T);
  }

  public void invokef(VM vm, ArcObject arg1, ArcObject arg2) {
    vm.pushA(Truth.valueOf(arg1.isSame(arg2)));
  }

  public ArcObject invoke(Pair args) {
    return checkIs(args.car(), args.cdr());
  }

  private ArcObject checkIs(ArcObject test, ArcObject args) {
    if (args instanceof Nil) {
      return T;
    }

    if (!test.isSame(args.car())) {
      return NIL;
    }

    return checkIs(test, args.cdr());
  }

}
