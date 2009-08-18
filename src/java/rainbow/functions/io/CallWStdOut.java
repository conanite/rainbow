package rainbow.functions.io;

import rainbow.functions.Builtin;
import rainbow.functions.IO;
import rainbow.types.Output;
import rainbow.types.Pair;
import rainbow.vm.VM;
import rainbow.vm.instructions.SetThreadLocal;

public class CallWStdOut extends Builtin {
  public CallWStdOut() {
    super("call-w/stdout");
  }

  public void invoke(VM vm, Pair args) {
    vm.pushFrame(new SetThreadLocal(IO.stdOut, IO.stdOut()));
    IO.stdOut.set(Output.cast(args.car(), this));
    args.cdr().car().invoke(vm, NIL);
  }
}
