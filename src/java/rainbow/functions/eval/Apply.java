package rainbow.functions.eval;

import rainbow.ArcError;
import rainbow.Nil;
import rainbow.functions.Builtin;
import rainbow.types.ArcObject;
import rainbow.types.Pair;
import rainbow.vm.VM;

public class Apply extends Builtin {
  public Apply() {
    super("apply");
  }

  public void invokef(VM vm) {
    throw new ArcError("apply: expects at least 2 args, got none");
  }

  public void invokef(VM vm, ArcObject arg) {
    throw new ArcError("apply: expects at least 2 args, got " + arg);
  }

  public void invokef(VM vm, ArcObject arg1, ArcObject arg2) {
    arg1.invoke(vm, (Pair) arg2);
  }

  public void invokef(VM vm, ArcObject arg1, ArcObject arg2, ArcObject arg3) {
    arg1.invoke(vm, new Pair(arg2, arg3));
  }

  public void invoke(VM vm, Pair args) {
    args.car().invoke(vm, constructApplyArgs((Pair) args.cdr()));
  }

  private Pair constructApplyArgs(Pair args) {
    if (args.cdr() instanceof Nil) {
      return Pair.cast(args.car(), this);
    } else {
      return new Pair(args.car(), constructApplyArgs(Pair.cast(args.cdr(), this)));
    }
  }
}
