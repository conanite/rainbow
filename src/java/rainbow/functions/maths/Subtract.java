package rainbow.functions.maths;

import rainbow.functions.Builtin;
import rainbow.types.ArcObject;
import rainbow.types.Pair;
import rainbow.types.ArcNumber;
import rainbow.Nil;
import rainbow.ArcError;
import rainbow.vm.VM;

public class Subtract extends Builtin {
  public Subtract() {
    super("-");
  }

  public void invokef(VM vm) {
    throw new ArcError("- : expected at least 1 arg");
  }

  public void invokef(VM vm, ArcObject arg) {
    vm.pushA(((ArcNumber)arg).negate());
  }

  public void invokef(VM vm, ArcObject arg1, ArcObject arg2) {
    vm.pushA(arg1.add(((ArcNumber)arg2).negate()));
  }

  public ArcObject invoke(Pair args) {
    if (args instanceof Nil) {
      throw new ArcError("Function `-` expected at least 1 arg");
    }

    ArcNumber first = ((ArcNumber) args.car()).negate();
    Pair rest = (Pair) args.cdr();

    if (rest instanceof Nil) {
      return first;
    }
    return Add.sum(new Pair(first, rest)).negate();
  }
}
