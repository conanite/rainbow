package rainbow.functions.io;

import rainbow.functions.Builtin;
import rainbow.functions.IO;
import rainbow.types.ArcObject;
import rainbow.types.Pair;
import rainbow.vm.VM;

public class StdIn extends Builtin {
  public StdIn() {
    super("stdin");
  }

  public void invokef(VM vm) {
    vm.pushA(IO.stdIn());
  }

  public ArcObject invoke(Pair args) {
    checkExactArgsCount(args, 0, getClass());
    return IO.stdIn();
  }
}
