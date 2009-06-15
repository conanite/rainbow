package rainbow.functions;

import rainbow.ArcError;
import rainbow.Environment;
import rainbow.types.*;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public abstract class Maths {
  private static final Random random = new Random();

  public static void collect(Environment bindings) {
    bindings.add(new Builtin[]{
      new Builtin("trunc") {
        public ArcObject invoke(Pair args) {
          checkMaxArgCount(args, getClass(), 1);
          double value = ((ArcNumber) args.car()).toDouble();
          return new Rational((long) Math.floor(value));
        }
      }, new Builtin("expt") {
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
      }, new Builtin("rand") {
        public ArcObject invoke(Pair args) {
          if (args.isNil()) {
            return new Real(random.nextDouble());
          } else {
            ArcNumber r = (ArcNumber) args.car();
            if (!r.isInteger()) {
              throw new ArcError("rand: requires one exact integer argument, got " + args);
            }
            return new Rational(Math.abs(random.nextLong() % r.toInt()));
          }
        }
      }, new Builtin("sqrt") {
        public ArcObject invoke(Pair args) {
          checkMaxArgCount(args, getClass(), 1);
          double result = Math.sqrt(ArcNumber.cast(args.car(), this).toDouble());
          if ((long)result == result) {
            return Rational.make((long) result);
          } else {
            return new Real(result);
          }
        }
      }, new Builtin("quotient") {
        public ArcObject invoke(Pair args) {
          checkExactArgsCount(args, 2, getClass());
          Rational top = Rational.cast(args.car(), this);
          Rational bottom = Rational.cast(args.cdr().car(), this);
          if (!(top.isInteger() && bottom.isInteger())) {
            throw new ArcError("Type error: " + this + " : expected integer, got " + args);
          }
          return Rational.make(top.toInt() / bottom.toInt());
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
          ArcNumber first = ((ArcNumber) args.car()).negate();
          Pair rest = (Pair) args.cdr();
          if (rest.isNil()) {
            return first;
          }
          return sum(new Pair(first, rest)).negate();
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

  public static void extra(Environment bindings) {
    ((Symbol) Symbol.make("pi")).setValue(new Real(Math.PI));

    bindings.add(new Builtin[] {
      new Builtin("sin") {
        protected ArcObject invoke(Pair args) {
          checkMaxArgCount(args, getClass(), 1);
          double result = Math.sin(ArcNumber.cast(args.car(), this).toDouble());
          return new Real(result);
        }
      },
      new Builtin("cos") {
        protected ArcObject invoke(Pair args) {
          checkMaxArgCount(args, getClass(), 1);
          double result = Math.cos(ArcNumber.cast(args.car(), this).toDouble());
          return new Real(result);
        }
      },
      new Builtin("tan") {
        protected ArcObject invoke(Pair args) {
          checkMaxArgCount(args, getClass(), 1);
          double result = Math.tan(ArcNumber.cast(args.car(), this).toDouble());
          return new Real(result);
        }
      },
      new Builtin("log") {
        protected ArcObject invoke(Pair args) {
          checkMaxArgCount(args, getClass(), 1);
          ArcNumber arg = ArcNumber.cast(args.car(), this);
          if (arg instanceof Complex) {
            return ((Complex)arg).log();
          } else {
            return new Real(Math.log(arg.toDouble()));
          }
        }
      },
      new Builtin("complex-parts") {
        protected ArcObject invoke(Pair args) {
          checkMaxArgCount(args, getClass(), 1);
          Complex x = Complex.cast(args.car(), this);
          return Pair.buildFrom(x.realPart(), x.imaginaryPart());
        }
      },
      new Builtin("make-complex") {
        protected ArcObject invoke(Pair args) {
          checkMaxArgCount(args, getClass(), 2);
          ArcNumber a = ArcNumber.cast(args.car(), this);
          ArcNumber b = ArcNumber.cast(args.cdr().car(), this);
          return new Complex(a, b);
        }
      },
      new Builtin("polar-coordinates") {
        protected ArcObject invoke(Pair args) {
          checkMaxArgCount(args, getClass(), 1);
          Complex x = Complex.cast(args.car(), this);
          return Pair.buildFrom(new Real(x.radius()), new Real(x.theta()));
        }
      }
    });
  }

  private static MathsOps precision(Pair args) {
    if (args.isNil()) {
      return rationalOps;
    } else if (args.car() instanceof Real) {
      return doubleOps;
    } else if (args.car() instanceof Complex) {
      return complexOps;
    } else {
      return precision((Pair) args.cdr());
    }
  }

  private static ArcNumber sum(Pair args) {
    return precision(args).sum(args);
  }

  private static ArcString concat(Pair args) {
    return ArcString.make(concatStrings(args));
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

  static MathsOps complexOps = new MathsOps() {
    public Complex sum(Pair args) {
      if (args.isNil()) {
        return Complex.ZERO;
      }
      return Complex.make(args.car()).plus(sum((Pair) args.cdr()));
    }

    public Complex multiply(Pair args) {
      if (args.isNil()) {
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
