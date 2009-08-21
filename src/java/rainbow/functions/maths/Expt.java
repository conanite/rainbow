package rainbow.functions.maths;

import rainbow.functions.Builtin;
import rainbow.types.*;

public class Expt extends Builtin {
  public Expt() {
    super("expt");
  }

  public ArcObject invoke(Pair args) {
    checkMaxArgCount(args, getClass(), 2);
    ArcNumber base = (ArcNumber) args.car();
    ArcNumber exp = (ArcNumber) args.cdr().car();
    if (base instanceof Complex) {
      return ((Complex)base).expt(exp);
    } else if (exp instanceof Complex) {
      Complex complexBase = new Complex(base, new Real(0.0));
      return complexBase.expt(exp);
    } else {
      double value = base.toDouble();
      double exponent = exp.toDouble();
      return new Real(Math.pow(value, exponent));
    }
  }
}
