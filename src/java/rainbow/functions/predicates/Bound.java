package rainbow.functions.predicates;

import rainbow.ArcError;
import rainbow.Truth;
import rainbow.functions.Builtin;
import rainbow.types.ArcObject;
import rainbow.types.Pair;
import rainbow.types.Symbol;
import rainbow.vm.VM;

public class Bound extends Builtin {
  public Bound() {
    super("bound");
  }

  public void invokef(VM vm) {
    throw new ArcError("bound: requires 1 arg");
  }

  public void invokef(VM vm, ArcObject arg) {
    vm.pushA(Truth.valueOf(((Symbol)arg).bound()));
  }

  public ArcObject invoke(Pair args) {
    checkMaxArgCount(args, getClass(), 1);
    Symbol sym = Symbol.cast(args.car(), this);
    return Truth.valueOf(sym.bound());
  }
}
