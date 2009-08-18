package rainbow.functions.fs;

import rainbow.functions.Builtin;
import rainbow.types.ArcObject;
import rainbow.types.Pair;
import rainbow.types.ArcString;
import rainbow.ArcError;
import rainbow.Truth;

import java.io.File;

public class MakeDirectory extends Builtin {
  public MakeDirectory() { super("make-directory"); }

  public ArcObject invoke(Pair args) {
    String path = ArcString.cast(args.car(), this).value();
    File f = new File(path);
    if (f.exists() && !f.isDirectory()) {
      throw new ArcError("make-directory: file exists and is not a directory: " + f);
    }

    return f.mkdir() ? Truth.T : Truth.NIL;
  }
}
