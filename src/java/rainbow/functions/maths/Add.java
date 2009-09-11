package rainbow.functions.maths;

import rainbow.Nil;
import rainbow.functions.Builtin;
import rainbow.types.ArcNumber;
import rainbow.types.ArcObject;
import rainbow.types.Pair;
import rainbow.types.Rational;
import rainbow.vm.VM;

public class Add extends Builtin {
  public Add() {
    super("+");
  }

  public void invokef(VM vm) {
    vm.pushA(Rational.ZERO);
  }

  public void invokef(VM vm, ArcObject arg) {
    vm.pushA(arg);
  }

  public void invokef(VM vm, ArcObject arg1, ArcObject arg2) {
    vm.pushA(arg1.add(arg2));
  }
  
  public void invokef(VM vm, ArcObject arg1, ArcObject arg2, ArcObject arg3) {
    vm.pushA(arg1.add(arg2).add(arg3));
  }

  public ArcObject invoke(Pair args) {
    if (args instanceof Nil) {
      return Rational.ZERO;
    }
    ArcObject result = args.car();
    ArcObject rest = args.cdr();
    while (!(rest instanceof Nil)) {
      result = result.add(rest.car());
      rest = rest.cdr();
    }
    return result;
  }

  public static ArcNumber sum(Pair args) {
    return Maths.precision(args).sum(args);
  }

}
