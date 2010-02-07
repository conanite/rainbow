package rainbow.functions.fs;

import rainbow.functions.Builtin;
import rainbow.types.ArcObject;
import rainbow.types.Pair;
import rainbow.types.ArcString;
import rainbow.ArcError;

import java.io.File;

public class RmFile extends Builtin {
  public RmFile() { super("rmfile"); }

  public ArcObject invoke(Pair args) {
    String path = ArcString.cast(args.car(), this).value();
    File f = new File(path);
    if (!f.delete()) {
      try {
        Thread.sleep(10);
      } catch (InterruptedException e) {
      }
      if (f.exists() && !f.delete()) {
        throw new ArcError("rmfile: unable to delete " + path);
      }
    }
    return NIL;
  }
}
