package rainbow.functions.maths;

import rainbow.functions.Builtin;
import rainbow.types.ArcObject;
import rainbow.types.Pair;
import rainbow.types.ArcNumber;
import rainbow.types.Rational;
import rainbow.vm.VM;

public class Trunc extends Builtin {
  public Trunc() {
    super("trunc");
  }

  public void invokef(VM vm, ArcObject arg) {
    double value = ((ArcNumber) arg).toDouble();
    vm.pushA(new Rational((long) Math.floor(value)));
  }

  public ArcObject invoke(Pair args) {
    checkMaxArgCount(args, getClass(), 1);
    double value = ((ArcNumber) args.car()).toDouble();
    return new Rational((long) Math.floor(value));
  }
}
