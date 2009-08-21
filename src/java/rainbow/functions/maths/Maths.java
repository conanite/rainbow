package rainbow.functions.maths;

import rainbow.Nil;
import rainbow.types.*;

import java.util.Random;

public abstract class Maths {
  public static final Random random = new Random();

  public static MathsOps precision(Pair args) {
    if (args instanceof Nil) {
      return rationalOps;
    } else if (args.car() instanceof Real) {
      return doubleOps;
    } else if (args.car() instanceof Complex) {
      return complexOps;
    } else {
      return precision((Pair) args.cdr());
    }
  }

  static interface MathsOps {
    ArcNumber sum(Pair args);

    ArcNumber multiply(Pair args);

    ArcNumber divide(Pair args);
  }

  static MathsOps doubleOps = new MathsOps() {
    public Real sum(Pair args) {
      return Real.make(sumNumbers(args));
    }

    private double sumNumbers(Pair args) {
      if (args instanceof Nil) {
        return 0;
      }
      return ((ArcNumber) args.car()).toDouble() + sumNumbers((Pair) args.cdr());
    }

    public Real multiply(Pair args) {
      return Real.make(multiplyDouble(args));
    }

    public ArcNumber divide(Pair args) {
      double first = (args.cdr() instanceof Nil) ? 1.0d / ((ArcNumber) args.car()).toDouble() : ((ArcNumber) args.car()).toDouble();
      return Real.make(first / multiplyDouble((Pair) args.cdr()));
    }

    private double multiplyDouble(Pair args) {
      if (args instanceof Nil) {
        return 1;
      }
      ArcObject left = args.car();

      double first = ((ArcNumber) left).toDouble();
      double rest = multiplyDouble((Pair) args.cdr());
      return first * rest;
    }
  };

  static MathsOps complexOps = new MathsOps() {
    public Complex sum(Pair args) {
      if (args instanceof Nil) {
        return Complex.ZERO;
      }
      return Complex.make(args.car()).plus(sum((Pair) args.cdr()));
    }

    public Complex multiply(Pair args) {
      if (args instanceof Nil) {
        return new Complex(1, 0);
      }

      return Complex.make(args.car()).times(multiply((Pair) args.cdr()));
    }

    public ArcNumber divide(Pair args) {
      return Complex.make(args.car()).times(multiply((Pair) args.cdr()).inverse());
    }
  };

  static MathsOps rationalOps = new MathsOps() {
    public Rational sum(Pair args) {
      if (args instanceof Nil) {
        return Rational.ZERO;
      } else {
        Rational r = (Rational) args.car();
        return r.plus(sum((Pair) args.cdr()));
      }
    }

    public Rational multiply(Pair args) {
      if (args instanceof Nil) {
        return Rational.ONE;
      }
      Rational left = (Rational) args.car();
      return left.times(multiply((Pair) args.cdr()));
    }

    public ArcNumber divide(Pair args) {
      Rational first = (args.cdr() instanceof Nil) ? ((Rational) args.car()).invert() : (Rational) args.car();
      return first.times(multiply((Pair) args.cdr()).invert());
    }
  };

}
