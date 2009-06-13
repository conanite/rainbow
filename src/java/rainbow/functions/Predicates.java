package rainbow.functions;

import rainbow.*;
import rainbow.vm.ArcThread;
import rainbow.vm.Continuation;
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
    public void invoke(ArcThread thread, LexicalClosure lc, Continuation caller, Pair args) {
      checkMaxArgCount(args, getClass(), 2);
      Symbol sym = Symbol.cast(args.car(), this);
      caller.receive(Truth.valueOf(sym.bound()));
    }
  }

  public static class Exact extends Builtin {
    public ArcObject invoke(Pair args) {
      ArcNumber arg = ArcNumber.cast(args.car(), this);
      return Truth.valueOf(arg instanceof Rational && arg.isInteger());
    }
  }

  public static class Is extends Builtin {
    public ArcObject invoke(Pair args) {
      return checkIs(args.car(), args.cdr());
    }

    private ArcObject checkIs(ArcObject test, ArcObject args) {
      if (args.isNil()) {
        return T;
      }

      if (!test.isSame(args.car())) {
        return NIL;
      }

      return checkIs(test, args.cdr());
    }

  }
}
