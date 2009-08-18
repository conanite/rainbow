package rainbow.functions.errors;

import rainbow.functions.Builtin;
import rainbow.types.Pair;
import rainbow.vm.VM;
import rainbow.vm.instructions.Catch;

public class OnErr extends Builtin {
  public OnErr() {
    super("on-err");
  }

  public void invoke(VM vm, Pair args) {
    vm.pushFrame(new Catch(args.car(), vm.ap()));
    args.cdr().car().invoke(vm, NIL);
  }
}
