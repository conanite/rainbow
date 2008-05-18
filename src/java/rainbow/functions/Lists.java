package rainbow.functions;

import rainbow.*;
import rainbow.types.*;

public abstract class Lists {
  public static class Car extends Builtin {
    public ArcObject invoke(Pair args) {
      checkMaxArgCount(args, getClass(), 1);
      Pair arg = (Pair) args.car();
      if (arg.isNil()) {
        return NIL;
      }
      return arg.car();
    }
  }

  public static class Cdr extends Builtin {
    public ArcObject invoke(Pair args) {
      checkMaxArgCount(args, getClass(), 1);
      return args.car().cdr();
    }
  }

  public static class Cons extends Builtin {
    public ArcObject invoke(Pair args) {
      checkExactArgsCount(args, 2, getClass());
      Pair pair = new Pair();
      pair.setCar(args.car());
      pair.setCdr(args.cdr().car());
      return pair;
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
      ArcObject object = args.car();
      ArcObject newValue = args.cdr().car();
      if (object instanceof ArcString) {
        scarString((ArcString)object, (ArcCharacter) newValue);
      } else if (object instanceof Pair) {
        ((Pair) object).setCar(newValue);
      }
      return newValue;
    }

    private void scarString(ArcString arcString, ArcCharacter newValue) {
      StringBuilder sb = new StringBuilder(newValue.stringValue());
      sb.append(arcString.value().substring(1));
      arcString.setValue(sb.toString());
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
      ArcObject arg = args.car();
      if (arg instanceof ArcString) {
        return lengthOf((ArcString)arg);
      } else if (arg instanceof Pair) {
        return lengthOf((Pair) arg);
      } else if (arg instanceof Hash) {
        return lengthOf((Hash)arg);
      }
      throw new ArcError("len: expects one string, list or hash argument, got " + args);
    }

    private ArcObject lengthOf(Hash hash) {
      return new Rational(hash.size(), 1);
    }

    private ArcObject lengthOf(Pair list) {
      return new Rational(list.size(), 1);
    }

    private ArcObject lengthOf(ArcString arcString) {
      return new Rational(arcString.value().length(), 1);
    }
  }
}
