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
    boolean deleted = f.delete();
    if (!deleted) {
      throw new ArcError("rmfile: unable to delete " + path);
    }
    return NIL;
  }
}
