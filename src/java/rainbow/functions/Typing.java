package rainbow.functions;

import rainbow.ArcError;
import rainbow.types.*;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class Typing {
  public static class Type extends Builtin {
    public ArcObject invoke(Pair args) {
      checkMaxArgCount(args, getClass(), 1);
      ArcObject arg = args.car();
      return arg.type();
    }
  }

  public static class Annotate extends Builtin {
    public ArcObject invoke(Pair args) {
      ArcObject type = args.car();
      ArcObject rep = args.cdr().car();
      return new Tagged(type, rep);
    }
  }

  public static class Rep extends Builtin {
    public ArcObject invoke(Pair args) {
      return cast(args.car(), Tagged.class).getRep();
    }
  }

  public static class Coerce extends Builtin {
    public ArcObject invoke(Pair args) {
      ArcObject toType = args.cdr().car();
      ArcObject arg = args.car();
      String fromType = arg.type().toString();
      if (fromType.equals(toType.toString())) {
        return arg;
      }
      String key = fromType + "-" + toType.toString();
      ArcNumber base = null;
      if (!args.cdr().cdr().isNil()) {
        base = (ArcNumber) args.cdr().cdr().car();
      }
      try {
        return coercion.get(key).coerce(arg, base);
      } catch (CantCoerce cc) {
        throw new ArcError("Can't coerce " + arg + " to " + toType);
      } catch (Exception e) {
        throw new ArcError("Can't coerce " + arg + " ( a " + arg.type() + " ) to " + toType, e);
      }
    }
  }

  private static Map<String, Coercer> coercion = new HashMap();

  static {
    coercion.put("string-cons", new Coercer() {
      public ArcObject coerce(ArcObject original, ArcNumber base) {
        List<ArcCharacter> chars = new LinkedList();
        String source = ((ArcString) original).value();
        for (int i = 0; i < source.length(); i++) {
          chars.add(new ArcCharacter(source.charAt(i)));
        }
        return Pair.buildFrom(chars.toArray(new ArcObject[chars.size()]));
      }
    });

    coercion.put("cons-string", new Coercer() {
      public ArcObject coerce(ArcObject original, ArcNumber base) {
        StringBuilder result = new StringBuilder();
        Pair chars = (Pair) original;
        while (!chars.isNil()) {
          result.append(((ArcCharacter) chars.car()).value());
          chars = (Pair) chars.cdr();
        }
        return ArcString.make(result.toString());
      }
    });

    coercion.put("int-string", new Coercer() {
      public ArcObject coerce(ArcObject original, ArcNumber base) {
        if (base == null) {
          base = Rational.TEN;
        }
        ArcNumber n = (ArcNumber) original;
        if (n instanceof Real && base.toInt() != 10) {
          return cantCoerce();
        } else if (n instanceof Rational) {
          return stringify((Rational) n, base);
        } else {
          return stringify((Real) n, base);
        }
      }
    });

    coercion.put("num-string", coercion.get("int-string"));

    coercion.put("string-sym", new Coercer() {
      public ArcObject coerce(ArcObject original, ArcNumber base) {
        String source = ((ArcString) original).value();
        if ("".equals(source)) {
          return Symbol.EMPTY_STRING;
        }
        return Symbol.make(source);
      }
    });

    coercion.put("sym-string", new Coercer() {
      public ArcObject coerce(ArcObject original, ArcNumber base) {
        String source = ((Symbol) original).name();
        return ArcString.make(source);
      }
    });

    coercion.put("char-int", new Coercer() {
      public ArcObject coerce(ArcObject original, ArcNumber base) {
        char source = ((ArcCharacter) original).value();
        return Rational.make(source);
      }
    });

    coercion.put("num-int", new Coercer() {
      public ArcObject coerce(ArcObject original, ArcNumber base) {
        ArcNumber source = (ArcNumber) original;
        return Real.make(source.toInt());
      }
    });

    coercion.put("int-char", new Coercer() {
      public ArcObject coerce(ArcObject original, ArcNumber base) {
        ArcNumber num = (ArcNumber) original;
        if (!num.isInteger()) {
          cantCoerce();
        }
        return new ArcCharacter((char) num.toInt());
      }
    });

    coercion.put("char-string", new Coercer() {
      public ArcObject coerce(ArcObject original, ArcNumber base) {
        char source = ((ArcCharacter) original).value();
        return ArcString.make(new String(new char[]{source}));
      }
    });

    coercion.put("char-sym", new Coercer() {
      public ArcObject coerce(ArcObject original, ArcNumber base) {
        char source = ((ArcCharacter) original).value();
        return Symbol.make(new String(new char[]{source}));
      }
    });

    coercion.put("string-int", new Coercer() {
      public ArcObject coerce(ArcObject original, ArcNumber base) {
        if (base == null) {
          base = Rational.TEN;
        }
        String source = ((ArcString) original).value();
        if (source.contains(".") || source.toUpperCase().contains("E")) {
          return coerceDouble(source, base.toInt());
        } else if (source.contains("/")) {
          return coerceFraction(source, base.toInt());
        } else {
          return coerceInt(source, base.toInt());
        }
      }
    });
  }

  private static ArcObject stringify(Rational rational, ArcNumber base) {
    String num = Long.toString(rational.numerator(), (int) base.toInt());
    if (rational.isInteger()) {
      return ArcString.make(num);
    } else {
      String den = Long.toString(rational.denominator(), (int) base.toInt());
      return ArcString.make(num + "/" + den);
    }
  }

  private static ArcObject stringify(Real d, ArcNumber base) {
    if (base.toInt() == 10) {
      return ArcString.make(d.toString());
    } else {
      String num = Long.toString(d.toInt(), (int) base.toInt());
      return ArcString.make(num + ".0");
    }
  }

  private static ArcObject coerceInt(String source, long base) {
    long value = Long.parseLong(source, (int) base);
    return Rational.make(value);
  }

  private static ArcObject coerceFraction(String source, long base) {
    String[] parts = source.split("/");
    long num = Long.parseLong(parts[0], (int) base);
    long div = Long.parseLong(parts[1], (int) base);
    return Rational.make(num, div);
  }

  private static ArcObject coerceDouble(String source, long base) {
    source = source.toUpperCase();
    if (!source.contains("E")) {
      source = source + "E0";
    }
    if (!source.contains(".")) {
      String[] parts = source.split("E");
      source = parts[0] + ".0E" + parts[1];
    }

    String[] parts = source.split("\\.");
    long integral = Long.parseLong(parts[0], (int) base);
    parts = parts[1].split("E");
    if (parts[1].startsWith("+")) {
      parts[1] = parts[1].substring(1);
    }
    double decimal = Long.parseLong(parts[0], (int) base) / Math.pow(base, parts[0].length());
    double exponent = Math.pow(base, Long.parseLong(parts[1], (int) base));

    double result = (integral + decimal) * exponent;
    return Real.make(result);
  }

  interface Coercer {
    ArcObject coerce(ArcObject original, ArcNumber base);
  }

  private static ArcObject cantCoerce() {
    throw new CantCoerce();
  }

  private static class CantCoerce extends RuntimeException {
  }
}