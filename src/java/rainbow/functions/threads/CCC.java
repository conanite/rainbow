package rainbow.functions.threads;

import rainbow.functions.Builtin;
import rainbow.vm.VM;
import rainbow.types.Pair;
import rainbow.types.ArcObject;

public class CCC extends Builtin {
  public CCC() {
    super("ccc");
  }

  public void invoke(VM vm, Pair args) {
    checkMaxArgCount(args, getClass(), 1);
    ContinuationWrapper e = new ContinuationWrapper(vm.copy());
    args.car().invoke(vm, Pair.buildFrom(e));
  }

  public static class ContinuationWrapper extends ArcObject {
    private final VM copy;

    public ContinuationWrapper(VM vm) {
      this.copy = vm;
    }

    public void invoke(VM vm, Pair args) {
      copy.copyTo(vm);
      vm.pushA(args.car());
    }

    public ArcObject type() {
      return TYPE;
    }
  }
}
