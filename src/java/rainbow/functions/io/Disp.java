package rainbow.functions.io;

import rainbow.functions.Builtin;
import rainbow.functions.IO;
import rainbow.types.*;

public class Disp extends Builtin {
  public Disp() {
    super("disp");
  }

  public ArcObject invoke(Pair args) {
    Output out = IO.chooseOutputPort(args.cdr().car(), this);
    ArcObject o = args.car();
    if (o instanceof ArcString) {
      out.write(((ArcString) o).value());
    } else if (o instanceof ArcCharacter) {
      out.writeChar((ArcCharacter) o);
    } else {
      out.write(o);
    }
    return NIL;
  }
}
