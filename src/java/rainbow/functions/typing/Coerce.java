package rainbow.functions.typing;

import rainbow.functions.Builtin;
import rainbow.types.ArcObject;
import rainbow.types.Pair;
import rainbow.types.ArcNumber;
import rainbow.vm.VM;

public class Coerce extends Builtin {
  public Coerce() {
    super("coerce");
  }

  public void invokef(VM vm, ArcObject arg, ArcObject toType) {
    vm.pushA(Typing.coerce(arg, toType, null));
  }

  public void invokef(VM vm, ArcObject arg, ArcObject toType, ArcObject base) {
    vm.pushA(Typing.coerce(arg, toType, (ArcNumber) base));
  }

  public ArcObject invoke(Pair args) {
    checkMinArgCount(args, getClass(), 2);
    checkMaxArgCount(args, getClass(), 3);
    return Typing.coerce(args);
  }
}
