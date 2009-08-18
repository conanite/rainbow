package rainbow.functions.errors;

import rainbow.functions.Builtin;
import rainbow.types.Pair;
import rainbow.vm.VM;
import rainbow.vm.instructions.FinallyInvoke;
import rainbow.vm.instructions.PopArg;

public class Protect extends Builtin {
  public Protect() {
    super("protect");
  }

  public void invoke(VM vm, Pair args) {
    vm.pushFrame(new PopArg("clear up 'protect/after' return value"));
    vm.pushFrame(new FinallyInvoke(args.cdr().car()));
    args.car().invoke(vm, NIL);
  }
}
