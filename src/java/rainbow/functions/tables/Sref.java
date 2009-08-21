package rainbow.functions.tables;

import rainbow.functions.Builtin;
import rainbow.types.ArcObject;
import rainbow.types.Pair;
import rainbow.vm.VM;
import rainbow.ArcError;

public class Sref extends Builtin {
  public Sref() {
    super("sref");
  }

  public void invokef(VM vm) {
    throw new ArcError("sref: requires 3 args, got none");
  }

  public void invokef(VM vm, ArcObject arg) {
    throw new ArcError("sref: requires 3 args, got one: " + arg);
  }

  public void invokef(VM vm, ArcObject arg1, ArcObject arg2) {
    throw new ArcError("sref: requires 3 args, got two: " + arg1 + ", " + arg2);
  }

  public void invokef(VM vm, ArcObject arg1, ArcObject arg2, ArcObject arg3) {
    vm.pushA(arg1.sref(arg2, arg3));
  }

  public ArcObject invoke(Pair args) {
    checkExactArgsCount(args, 3, getClass());
    return args.car().sref((Pair) args.cdr());
  }
}
