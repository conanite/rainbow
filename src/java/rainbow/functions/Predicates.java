package rainbow.functions;

import rainbow.*;
import rainbow.types.*;

public abstract class Predicates {

  public static class LessThan extends Builtin {
    public ArcObject invoke(Pair args) {
      ArcObject left = args.car();
      Pair others = (Pair) args.cdr();
      while (!others.isNil()) {
        ArcObject right = others.car();
        int comparison = left.compareTo(right);
        if (comparison >= 0) {
          return NIL;
        }
        left = right;
        others = (Pair) others.cdr();
      }
      return T;
    }
  }

  public static class GreaterThan extends Builtin {
    public ArcObject invoke(Pair args) {
      ArcObject left = args.car();
      Pair others = (Pair) args.cdr();
      while (!others.isNil()) {
        ArcObject right = others.car();
        int comparison = left.compareTo(right);
        if (comparison <= 0) {
          return NIL;
        }
        left = right;
        others = (Pair) others.cdr();
      }
      return T;
    }
  }

  public static class Bound extends Builtin {
    public ArcObject invoke(Pair args, Bindings bindings) {
      checkMaxArgCount(args, getClass(), 2);
      Symbol sym = cast(args.car(), Symbol.class);
      ArcObject o = bindings.getTop().lookup(sym.name());
      return o != null ? T: NIL;
    }
  }

  public static class Exact extends Builtin {
    public ArcObject invoke(Pair args) {
      ArcNumber arg = cast(args.car(), ArcNumber.class);
      return Truth.valueOf(arg instanceof Rational && arg.isInteger());
    }
  }

  public static class Is extends Builtin {
    public ArcObject invoke(Pair args) {
      checkMinArgCount(args, getClass(), 1);
      return checkIs(args.car(), (Pair) args.cdr());
    }

    private ArcObject checkIs(ArcObject test, Pair args) {
      if (args.isNil()) {
        return T;
      }

      if (!test.equals(args.car())) {
        return NIL;
      }

      return checkIs(test, (Pair) args.cdr());
    }

  }
}
