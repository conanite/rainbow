package rainbow.functions.typing;

import rainbow.functions.Builtin;
import rainbow.types.ArcObject;
import rainbow.types.Pair;
import rainbow.types.Tagged;

public class Annotate extends Builtin {
  public Annotate() {
    super("annotate");
  }

  public ArcObject invoke(Pair args) {
    ArcObject type = args.car();
    ArcObject rep = args.cdr().car();
    if (type == rep.type()) {
      return rep;
    } else {
      return new Tagged(type, rep);
    }
  }
}
