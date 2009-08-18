package rainbow.functions.fs;

import rainbow.functions.Builtin;
import rainbow.types.ArcObject;
import rainbow.types.Pair;
import rainbow.types.FileInputPort;
import rainbow.types.ArcString;

public class InFile extends Builtin {
  public InFile() { super("infile"); }

  public ArcObject invoke(Pair args) {
    return new FileInputPort(ArcString.cast(args.car(), this).value());
  }
}
