package rainbow.functions.io;

import rainbow.functions.Builtin;
import rainbow.functions.IO;
import rainbow.types.Input;
import rainbow.types.Pair;
import rainbow.vm.VM;
import rainbow.vm.instructions.SetThreadLocal;

public class CallWStdIn extends Builtin {
  public CallWStdIn() {
    super("call-w/stdin");
  }

  public void invoke(VM vm, Pair args) {
    SetThreadLocal i = new SetThreadLocal(IO.stdIn, IO.stdIn());
    i.belongsTo(this);
    vm.pushFrame(i);
    IO.stdIn.set(Input.cast(args.car(), this));
    args.cdr().car().invoke(vm, NIL);
  }
}
