package rainbow.functions;

import rainbow.ArcError;
import rainbow.Environment;
import rainbow.Truth;
import rainbow.types.*;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FileSystem {
  public static void collect(Environment e) {
    e.add(new Builtin[]{
            new OutFile(),
            new InFile(),
            new DirExists(),
            new FileExists(),
            new Dir(),
            new RmFile(),
            new MakeDirectory(),
            new MakeDirectories()
    });
  }

  public static class OutFile extends Builtin {
    public OutFile() { super("outfile"); }

    public ArcObject invoke(Pair args) {
      String name = ArcString.cast(args.car(), this).value();
      ArcObject appendSymbol = args.cdr().car();
      boolean append = !appendSymbol.isNil() && Symbol.cast(appendSymbol, this).name().equals("append");
      return new FileOutputPort(name, append);
    }
  }

  public static class InFile extends Builtin {
    public InFile() { super("infile"); }

    public ArcObject invoke(Pair args) {
      return new FileInputPort(ArcString.cast(args.car(), this).value());
    }
  }

  public static class DirExists extends Builtin {
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

  public static class FileExists extends Builtin {
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

  public static class Dir extends Builtin {
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

  public static class RmFile extends Builtin {
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

  public static class MakeDirectory extends Builtin {
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

  public static class MakeDirectories extends Builtin {
    public MakeDirectories() { super("make-directory*"); }

    public ArcObject invoke(Pair args) {
      String path = ArcString.cast(args.car(), this).value();
      File f = new File(path);
      if (f.exists() && !f.isDirectory()) {
        throw new ArcError("make-directory: file exists and is not a directory: " + f);
      }

      return f.mkdirs() ? Truth.T : Truth.NIL;
    }
  }
}
