package rainbow.functions.io;

import rainbow.functions.Builtin;
import rainbow.functions.IO;
import rainbow.types.ArcObject;
import rainbow.types.Pair;
import rainbow.vm.VM;

public class ReadC extends Builtin {
  public ReadC() {
    super("readc");
  }

  public void invokef(VM vm, ArcObject arg) {
    vm.pushA(IO.chooseInputPort(arg, this).readCharacter());
  }

  public ArcObject invoke(Pair args) {
    return IO.chooseInputPort(args.car(), this).readCharacter();
  }
}
