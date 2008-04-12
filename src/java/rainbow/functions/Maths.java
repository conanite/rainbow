package rainbow.functions;

import rainbow.ArcError;
import rainbow.TopBindings;
import rainbow.types.*;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class Maths {
  private static final Random random = new Random();

  public static void collect(TopBindings bindings) {
    bindings.add(new Builtin[]{
            new Builtin("trunc") {
              public ArcObject invoke(Pair args) {
                checkMaxArgCount(args, getClass(), 1);
                double value = ((ArcNumber) args.car()).toDouble();
                return new Rational((long) Math.floor(value), 1);
              }
            }, new Builtin("expt") {
      public ArcObject invoke(Pair args) {
        checkMaxArgCount(args, getClass(), 2);
        double value = ((ArcNumber) args.car()).toDouble();
        double exponent = ((ArcNumber) args.cdr().car()).toDouble();
        return new Real(Math.pow(value, exponent));
      }
    }, new Builtin("rand") {
      public ArcObject invoke(Pair args) {
        if (args.isNil()) {
          return new Real(random.nextDouble());
        } else {
          ArcNumber r = (ArcNumber) args.car();
          if (!r.isInteger()) {
            throw new ArcError("rand: requires one exact integer argument, got " + args);
          }
          return new Rational(Math.abs(random.nextLong() % r.toInt()), 1);
        }
      }
    }, new Builtin("sqrt") {
      public ArcObject invoke(Pair args) {
        checkMaxArgCount(args, getClass(), 1);
        double value = cast(args.car(), ArcNumber.class).toDouble();
        return new Real(Math.sqrt(value));
      }
    }, new Builtin("mod") {
      public ArcObject invoke(Pair args) {
        checkMaxArgCount(args, getClass(), 2);
        ArcNumber first = (ArcNumber) args.car();
        ArcNumber second = (ArcNumber) args.cdr().car();
        if (!first.isInteger() || !second.isInteger()) {
          throw new ArcError("modulo: expects integer, got " + args);
        }
        long numerator = first.toInt();
        long divisor = second.toInt();
        long result = numerator % divisor;
        if (result < 0) {
          result += divisor;
        }
        return Rational.make(result);
      }
    }, new Builtin("+") {
      public ArcObject invoke(Pair args) {
        if (args.car() instanceof ArcNumber) {
          return sum(args);
        } else if (args.car() instanceof ArcString) {
          try {
            return concat(args);
          } catch (Exception e) {
            e.printStackTrace(System.out);
            throw new ArcError("Adding " + args, e);
          }
        } else if (args.car() instanceof Pair) {
          return joinLists(args);
        } else {
          throw new ArcError("Cannot sum " + args);
        }
      }
    }, new Builtin("-") {
      public ArcObject invoke(Pair args) {
        Pair pair = new Pair(((ArcNumber) args.car()).negate(), args.cdr());
        return sum(pair).negate();
      }
    }, new Builtin("*") {
      public ArcObject invoke(Pair args) {
        return precision(args).multiply(args);
      }
    }, new Builtin("/") {
      public ArcObject invoke(Pair args) {
        return precision(args).divide(args);
      }
    }
    });
  }

  private static MathsOps precision(Pair args) {
    if (args.isNil()) {
      return rationalOps;
    } else if (args.car() instanceof Real) {
      return doubleOps;
    } else {
      return precision((Pair) args.cdr());
    }
  }

  private static ArcNumber sum(Pair args) {
    return precision(args).sum(args);
  }

  private static ArcString concat(Pair args) {
    return new ArcString(concatStrings(args));
  }

  private static String concatStrings(Pair args) {
    return (args.isNil()) ? "" : ((ArcString) args.car()).value() + concatStrings((Pair) args.cdr());
  }

  private static Pair joinLists(Pair args) {
    List list = new LinkedList();
    copyAllTo(args, list);
    return Pair.buildFrom(list, ArcObject.NIL);
  }

  private static void copyAllTo(Pair args, List list) {
    if (args.isNil()) {
      return;
    }
    ((Pair) args.car()).copyTo(list);
    copyAllTo((Pair) args.cdr(), list);
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
      if (args.isNil()) {
        return 0;
      }
      return ((ArcNumber) args.car()).toDouble() + sumNumbers((Pair) args.cdr());
    }

    public Real multiply(Pair args) {
      return Real.make(multiplyDouble(args));
    }

    public ArcNumber divide(Pair args) {
      double first = ((ArcNumber) args.car()).toDouble();
      return Real.make(first / multiplyDouble((Pair) args.cdr()));
    }

    private double multiplyDouble(Pair args) {
      if (args.isNil()) {
        return 1;
      }
      ArcObject left = args.car();

      double first = ((ArcNumber) left).toDouble();
      double rest = multiplyDouble((Pair) args.cdr());
      return first * rest;
    }
  };

  static MathsOps rationalOps = new MathsOps() {
    public Rational sum(Pair args) {
      if (args.isNil()) {
        return Rational.ZERO;
      } else {
        Rational r = (Rational) args.car();
        return r.plus(sum((Pair) args.cdr()));
      }
    }

    public Rational multiply(Pair args) {
      if (args.isNil()) {
        return Rational.ONE;
      }
      Rational left = (Rational) args.car();
      return left.times(multiply((Pair) args.cdr()));
    }

    public ArcNumber divide(Pair args) {
      Rational first = (Rational) args.car();
      return first.times(multiply((Pair) args.cdr()).invert());
    }
  };
}
