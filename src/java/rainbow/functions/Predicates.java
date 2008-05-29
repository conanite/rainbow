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
      ArcObject o = thread.environment().lookup(sym);
      caller.receive(Truth.valueOf(o != null));
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
