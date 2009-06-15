package rainbow.functions;

import rainbow.ArcError;
import rainbow.types.*;

public abstract class Lists {
  public static class Car extends Builtin {
    public ArcObject invoke(Pair args) {
      try {
        args.cdr().mustBeNil();
      } catch (NotNil notNil) {
        throw new ArcError("car: expects only one argument: got " + args);
      }
      return args.car().car();
    }
  }

  public static class Cdr extends Builtin {
    public ArcObject invoke(Pair args) {
      try {
        args.cdr().mustBeNil();
      } catch (NotNil notNil) {
        throw new ArcError("cdr: expects only one argument: got " + args);
      }
      return args.car().cdr();
    }
  }

  public static class Cons extends Builtin {
    public ArcObject invoke(Pair args) {
      checkExactArgsCount(args, 2, getClass());
      return new Pair(args.car(), args.cdr().car());
    }
  }

  public static class NewString extends Builtin {
    public ArcObject invoke(Pair args) {
      ArcNumber n = (ArcNumber) args.car();
      ArcCharacter c = ArcCharacter.NULL;
      if (!args.cdr().isNil()) {
        c = (ArcCharacter) args.cdr().car();
      }
      StringBuilder b = new StringBuilder();
      for (int i = 0; i < n.toInt(); i++) {
        b.append(c.value());
      }
      return ArcString.make(b.toString());
    }
  }

  public static class Scar extends Builtin {
    public ArcObject invoke(Pair args) {
      return args.car().scar(args.cdr().car());
    }
  }

  public static class Scdr extends Builtin {
    public ArcObject invoke(Pair args) {
      Pair target = (Pair) args.car();
      ArcObject newValue = args.cdr().car();
      target.setCdr(newValue);
      return newValue;
    }
  }

  public static class Len extends Builtin {
    public ArcObject invoke(Pair args) {
      checkMaxArgCount(args, getClass(), 1);
      return new Rational(args.car().len());
    }
  }
}
