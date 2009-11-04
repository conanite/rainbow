package rainbow.functions.io;

import rainbow.functions.Builtin;
import rainbow.functions.IO;
import rainbow.types.*;
import rainbow.vm.VM;

public class Disp extends Builtin {
  public Disp() {
    super("disp");
  }

  public void invokef(VM vm, ArcObject arg) {
    disp(IO.stdOut(), arg);
    vm.pushA(NIL);
  }
  
  public ArcObject invoke(Pair args) {
    disp(IO.chooseOutputPort(args.cdr().car(), this), args.car());
    return NIL;
  }

  private void disp(Output out, ArcObject o) {
    if (o instanceof ArcString) {
      out.write(((ArcString) o).value());
    } else if (o instanceof ArcCharacter) {
      out.writeChar((ArcCharacter) o);
    } else {
      out.write(o);
    }
  }
}
