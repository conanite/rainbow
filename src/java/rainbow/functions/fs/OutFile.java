package rainbow.functions.fs;

import rainbow.functions.Builtin;
import rainbow.types.*;

public class OutFile extends Builtin {
  public OutFile() { super("outfile"); }

  public ArcObject invoke(Pair args) {
    String name = ArcString.cast(args.car(), this).value();
    ArcObject appendSymbol = args.cdr().car();
    boolean append = !appendSymbol.isNil() && Symbol.cast(appendSymbol, this).name().equals("append");
    return new FileOutputPort(name, append);
  }
}
