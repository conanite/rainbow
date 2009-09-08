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
    Pair instructions = new Pair(
            new FinallyInvoke(args.cdr().car()), new Pair(
            new PopArg("clear up 'protect/after' return value"),
            NIL));
    vm.pushInvocation(null, instructions);
    args.car().invoke(vm, NIL);
  }
}
