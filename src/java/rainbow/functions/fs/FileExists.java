package rainbow.functions.fs;

import rainbow.functions.Builtin;
import rainbow.types.ArcObject;
import rainbow.types.Pair;
import rainbow.types.ArcString;

import java.io.File;

public class FileExists extends Builtin {
  public FileExists() { super("file-exists"); }

  public ArcObject invoke(Pair args) {
    String path = ArcString.cast(args.car(), this).value();
    File file = new File(path);
    if (file.exists() && !file.isDirectory()) {
      return args.car();
    } else {
      return NIL;
    }
  }
}
