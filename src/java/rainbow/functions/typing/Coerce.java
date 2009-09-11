package rainbow.functions.typing;

import rainbow.ArcError;
import rainbow.functions.Builtin;
import rainbow.functions.typing.Typing.CantCoerce;
import rainbow.types.ArcObject;
import rainbow.types.Pair;
import rainbow.types.Symbol;
import rainbow.vm.VM;

public class Coerce extends Builtin {
  public Coerce() {
    super("coerce");
    Typing.init();
  }

  public void invokef(VM vm, ArcObject arg, ArcObject toType) {
    ArcObject fromType = arg.type();
    if (fromType == toType) {
      vm.pushA(arg);
      return;
    }
    try {
      ArcObject coercer = ((Symbol)toType).getCoercion((Symbol) fromType);
      coercer.invokef(vm, arg);
    } catch (CantCoerce cc) {
      throw new ArcError("Can't coerce " + arg + " to " + toType);
    } catch (Exception e) {
      throw new ArcError("Can't coerce " + arg + " ( a " + arg.type() + " ) to " + toType, e);
    }
  }

  public void invokef(VM vm, ArcObject arg, ArcObject toType, ArcObject base) {
    ArcObject fromType = arg.type();
    if (fromType == toType) {
      vm.pushA(arg);
      return;
    }
    try {
      ArcObject coercer = ((Symbol)toType).getCoercion((Symbol) fromType);
      coercer.invokef(vm, arg, base);
    } catch (CantCoerce cc) {
      throw new ArcError("Can't coerce " + arg + " to " + toType);
    } catch (Exception e) {
      throw new ArcError("Can't coerce " + arg + " ( a " + arg.type() + " ) to " + toType, e);
    }
  }

  public void invoke(VM vm, Pair args) {
    if (args.hasLen(2)) {
      invokef(vm, args.car(), args.cdr().car());
    } else if (args.hasLen(3)) {
      invokef(vm, args.car(), args.cdr().car(), args.cdr().cdr().car());
    } else {
      throw new ArcError("coerce expects 2 or 3 args, got " + args);
    }
  }
}
