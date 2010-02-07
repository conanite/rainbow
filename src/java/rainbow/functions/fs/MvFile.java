package rainbow.functions.fs;

import rainbow.ArcError;
import rainbow.functions.Builtin;
import rainbow.types.ArcObject;
import rainbow.types.ArcString;
import rainbow.types.Pair;

import javax.swing.*;
import java.io.File;

public class MvFile extends Builtin {
  public MvFile() { super("mvfile"); }

  public ArcObject invoke(Pair args) {
    String opath = ArcString.cast(args.car(), this).value();
    String npath = ArcString.cast(args.cdr().car(), this).value();
    File of = new File(opath);
    File nf = new File(npath);
    try {
      of.renameTo(nf);
    } catch (Exception e) {
      throw new ArcError("couldn't rename " + of + " to " + nf + ": " + e, e);
    }

    JTree tree;
    tree.getSelectionPath()
    return NIL;
  }
}
