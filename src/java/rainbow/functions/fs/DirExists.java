package rainbow.functions.fs;

import rainbow.functions.Builtin;
import rainbow.types.ArcObject;
import rainbow.types.Pair;
import rainbow.types.ArcString;

import java.io.File;

public class DirExists extends Builtin {
  public DirExists() { super("dir-exists"); }

  public ArcObject invoke(Pair args) {
    String path = ArcString.cast(args.car(), this).value();
    File dir = new File(path);
    if (!(dir.exists() && dir.isDirectory())) {
      return NIL;
    } else {
      return args.car();
    }
  }
}
