package rainbow.functions.typing;

import rainbow.functions.Builtin;
import rainbow.types.ArcObject;
import rainbow.types.Pair;
import rainbow.LexicalClosure;

public class Ref extends Builtin {
  public Ref() {
    super("ref");
  }

  public ArcObject invoke(LexicalClosure lc, Pair args) {
    return args.car().refFn().invoke(lc, args);
  }
}
