package rainbow.functions.fs;

import rainbow.functions.Builtin;
import rainbow.types.ArcObject;
import rainbow.types.Pair;
import rainbow.types.ArcString;
import rainbow.ArcError;

import java.io.File;
import java.util.List;
import java.util.ArrayList;

public class Dir extends Builtin {
  public Dir() { super("dir"); }

  public ArcObject invoke(Pair args) {
    String path = ArcString.cast(args.car(), this).value();
    File dir = new File(path);
    if (!dir.isDirectory()) {
      throw new ArcError("dir: '" + path + "' is not a directory");
    }
    String[] contents = dir.list();
    List results = new ArrayList(contents.length);
    for (String file : contents) {
      results.add(ArcString.make(file));
    }
    return Pair.buildFrom(results);
  }
}
