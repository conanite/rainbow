package rainbow.functions;

import rainbow.types.*;
import rainbow.ArcError;

import java.io.File;
import java.util.List;
import java.util.LinkedList;
import java.util.ArrayList;

public class FileSystem {
  public static class OutFile extends Builtin {
    public ArcObject invoke(Pair args) {
      String name = ArcString.cast(args.car(), this).value();
      ArcObject appendSymbol = args.cdr().car();
      boolean append = !appendSymbol.isNil() && Symbol.cast(appendSymbol, this).name().equals("append");
      return new FileOutputPort(name, append);
    }
  }

  public static class InFile extends Builtin {
    public ArcObject invoke(Pair args) {
      return new FileInputPort(ArcString.cast(args.car(), this).value());
    }
  }

  public static class DirExists extends Builtin {
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

  public static class FileExists extends Builtin {
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

  public static class Dir extends Builtin {
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

  public static class RmFile extends Builtin {
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
}
