package rainbow.functions.maths;

import rainbow.functions.Builtin;
import rainbow.types.ArcObject;
import rainbow.types.Pair;
import rainbow.types.Rational;
import rainbow.vm.VM;
import rainbow.Nil;

public class Multiply extends Builtin {
  public Multiply() {
    super("*");
  }

  public void invokef(VM vm) {
    vm.pushA(Rational.ONE);
  }

  public void invokef(VM vm, ArcObject arg) {
    vm.pushA(arg);
  }

  public void invokef(VM vm, ArcObject arg1, ArcObject arg2) {
    vm.pushA(arg1.multiply(arg2));
  }

  public void invokef(VM vm, ArcObject arg1, ArcObject arg2, ArcObject arg3) {
    vm.pushA(arg1.multiply(arg2).multiply(arg3));
  }

  public ArcObject invoke(Pair args) {
    if (args instanceof Nil) {
      return Rational.ZERO;
    }
    ArcObject result = args.car();
    ArcObject rest = args.cdr();
    while (!(rest instanceof Nil)) {
      result = result.multiply(rest.car());
      rest = rest.cdr();
    }
    return result;

//    return Maths.precision(args).multiply(args);
  }
}
