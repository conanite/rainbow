package rainbow.functions.maths;

import rainbow.functions.Builtin;
import rainbow.types.*;

public class Logarithm extends Builtin {
  public Logarithm() {
    super("log");
  }

  protected ArcObject invoke(Pair args) {
    checkMaxArgCount(args, getClass(), 1);
    ArcNumber arg = ArcNumber.cast(args.car(), this);
    if (arg instanceof Complex) {
      return ((Complex)arg).log();
    } else {
      return new Real(Math.log(arg.toDouble()));
    }
  }
}
