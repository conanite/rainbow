package rainbow.functions.io;

import rainbow.functions.Builtin;
import rainbow.functions.IO;
import rainbow.types.ArcObject;
import rainbow.types.Pair;
import rainbow.vm.VM;

public class ReadB extends Builtin {
  public ReadB() {
    super("readb");
  }

  public void invokef(VM vm, ArcObject arg) {
    vm.pushA(IO.chooseInputPort(arg, this).readByte());
  }

  public ArcObject invoke(Pair args) {
    return IO.chooseInputPort(args.car(), this).readByte();
  }
}
