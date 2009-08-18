package rainbow.functions.io;

import rainbow.functions.Builtin;
import rainbow.functions.IO;
import rainbow.types.ArcObject;
import rainbow.types.Pair;
import rainbow.LexicalClosure;

public class StdIn extends Builtin {
  public StdIn() {
    super("stdin");
  }

  public ArcObject invoke(LexicalClosure lc, Pair args) {
    return IO.stdIn();
  }
}
