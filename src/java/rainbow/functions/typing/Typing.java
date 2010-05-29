package rainbow.functions.typing;

import rainbow.ArcError;
import rainbow.Nil;
import rainbow.vm.VM;
import rainbow.parser.ParseException;
import rainbow.types.*;

import java.util.ArrayList;
import java.util.List;

public class Typing {
  public static Symbol STRING = Symbol.mkSym("string");
  public static Symbol SYM = Symbol.mkSym("sym");
  public static Symbol INT = Symbol.mkSym("int");
  public static Symbol NUM = Symbol.mkSym("num");
  public static Symbol CONS = Symbol.mkSym("cons");
  public static Symbol CHAR = Symbol.mkSym("char");

  static void init() {
    CONS.addCoercion(STRING, new Coercion("string-cons") {
      public ArcObject coerce(ArcObject arg) {
        String source = ((ArcString) arg).value();
        List<ArcCharacter> chars = new ArrayList(source.length());
        for (int i = 0; i < source.length(); i++) {
          chars.add(ArcCharacter.make(source.charAt(i)));
        }
        return Pair.buildFrom(chars.toArray(new ArcObject[chars.size()]));
      }
    });

    STRING.addCoercion(CONS, new Coercion("cons-string") {
      public ArcObject coerce(ArcObject arg) {
        StringBuilder result = new StringBuilder();
        Pair items = (Pair) arg;
        while (!(items instanceof Nil)) {
          result.append(items.car().disp());
          items = (Pair) items.cdr();
        }
        return ArcString.make(result.toString());
      }
    });

    STRING.addCoercion(INT, new Coercion("int-string") {
      public ArcObject coerce(ArcObject arg) {
        return coerce(arg, Rational.TEN);
      }

      public ArcObject coerce(ArcObject original, ArcObject arg2) {
        ArcNumber base = (ArcNumber) arg2;
        ArcNumber n = (ArcNumber) original;
        if (n instanceof Complex) {
          return stringify((Complex) n);
        } else if (n instanceof Real && base.toInt() != 10) {
          return cantCoerce();
        } else if (n instanceof Rational) {
          return stringify((Rational) n, base);
        } else {
          return stringify((Real) n, base);
        }
      }
    });

    STRING.addCoercion(NUM, new Coercion("num-string") {
      public ArcObject coerce(ArcObject arg) {
        return coerce(arg, Rational.TEN);
      }

      public ArcObject coerce(ArcObject original, ArcObject arg2) {
        ArcNumber base = (ArcNumber) arg2;
        ArcNumber n = (ArcNumber) original;
        if (n instanceof Complex) {
          return stringify((Complex) n);
        } else if (n instanceof Real && base.toInt() != 10) {
          return cantCoerce();
        } else if (n instanceof Rational) {
          return stringify((Rational) n, base);
        } else {
          return stringify((Real) n, base);
        }
      }
    });

    SYM.addCoercion(STRING, new Coercion("string-sym") {
      public ArcObject coerce(ArcObject original) {
        String source = ((ArcString) original).value();
        if ("".equals(source)) {
          return Symbol.EMPTY_STRING;
        }
        return Symbol.make(source);
      }
    });

    STRING.addCoercion(SYM, new Coercion("sym-string") {
      public ArcObject coerce(ArcObject original) {
        if (original instanceof Nil) {
          return ArcString.make("");
        }
        String source = original.toString();
        return ArcString.make(source);
      }
    });

    INT.addCoercion(CHAR, new Coercion("char-int") {
      public ArcObject coerce(ArcObject original) {
        char source = ((ArcCharacter) original).value();
        return Rational.make(source);
      }

      public ArcObject coerce(ArcObject original, ArcObject unused) {
        return coerce(original);
      }
    });

    INT.addCoercion(NUM, new Coercion("num-int") {
      public ArcObject coerce(ArcObject original) {
        return ((ArcNumber) original).round();
      }

      public ArcObject coerce(ArcObject original, ArcObject unused) {
        return coerce(original);
      }
    });

    NUM.addCoercion(INT, new Coercion("int-num") {
      public ArcObject coerce(ArcObject original) {
        return original;
      }
    });

    CHAR.addCoercion(INT, new Coercion("int-char") {
      public ArcObject coerce(ArcObject original) {
        ArcNumber num = (ArcNumber) original;
        if (!num.isInteger()) {
          cantCoerce();
        }
        return ArcCharacter.make((char) num.toInt());
      }
    });

    STRING.addCoercion(CHAR, new Coercion("char-string") {
      public ArcObject coerce(ArcObject original) {
        char source = ((ArcCharacter) original).value();
        return ArcString.make(new String(new char[]{source}));
      }
    });

    SYM.addCoercion(CHAR, new Coercion("char-sym") {
      public ArcObject coerce(ArcObject original) {
        char source = ((ArcCharacter) original).value();
        return Symbol.make(new String(new char[]{source}));
      }
    });

    NUM.addCoercion(STRING, new Coercion("string-num") {
      public ArcObject coerce(ArcObject original) {
        return coerce(original, Rational.TEN);
      }

      public ArcObject coerce(ArcObject original, ArcObject b) {
        ArcNumber base = (ArcNumber) b;
        String source = ((ArcString) original).value().toLowerCase();
        if (source.equals("+inf.0")) {
          return Real.positiveInfinity();
        } else if (source.equals("-inf.0")) {
          return Real.negativeInfinity();
        } else if (source.equals("+nan.0")) {
          return Real.nan();
        } else if (source.toLowerCase().endsWith("i")) {
          return coerceComplex(source);
        } else if (source.contains(".") || (base.toInt() < 15 && source.matches(".+[eE].+"))) {
          return coerceDouble(source, base.toInt());
        } else if (source.contains("/")) {
          return coerceFraction(source, base.toInt());
        } else {
          return coerceInt(source, base.toInt());
        }
      }
    });

    INT.addCoercion(STRING, new Coercion("string-int") {
      public ArcObject coerce(ArcObject original) {
        return coerce(original, Rational.TEN);
      }

      public ArcObject coerce(ArcObject original, ArcObject b) {
        ArcNumber base = (ArcNumber) b;
        String source = ((ArcString) original).value().toLowerCase();
        if (source.equals("+inf.0") || source.equals("-inf.0") || source.equals("+nan.0") || source.toLowerCase().endsWith("i")) {
          throw new CantCoerce();
        } else if (source.contains(".") || (base.toInt() < 15 && source.matches(".+[eE].+"))) {
          return ((Real) coerceDouble(source, base.toInt())).roundJava();
        } else if (source.contains("/")) {
          return ((Rational) coerceFraction(source, base.toInt())).roundJava();
        } else {
          return coerceInt(source, base.toInt());
        }
      }
    });
  }

  private static ArcObject stringify(Complex c) {
    return ArcString.make(c.toString());
  }

  private static ArcObject stringify(Rational rational, ArcNumber base) {
    return rational.stringify(base);
  }

  private static ArcObject stringify(Real d, ArcNumber base) {
    if (base.toInt() == 10) {
      return ArcString.make(d.toString());
    } else {
      String num = Long.toString(d.toInt(), (int) base.toInt());
      return ArcString.make(num + ".0");
    }
  }

  private static ArcObject coerceComplex(String source) {
    try {
      return Complex.parse(source);
    } catch (ParseException e) {
      throw new CantCoerce();
    }
  }

  private static ArcObject coerceInt(String source, long base) {
    long value = Long.parseLong(source, (int) base);
    return Rational.make(value);
  }

  private static ArcObject coerceFraction(String source, long base) {
    String[] parts = source.split("/");
    if (parts.length > 2) {
      throw new CantCoerce();
    }
    long num = Long.parseLong(parts[0], (int) base);
    long div = Long.parseLong(parts[1], (int) base);
    return Rational.make(num, div);
  }

  private static ArcObject coerceDouble(String source, long base) {
    boolean negative = false;
    if (source.startsWith("-")) {
      negative = true;
      source = source.substring(1);
    }
    source = source.toUpperCase();
    if (!source.contains("E")) {
      source = source + "E0";
    }
    if (!source.contains(".")) {
      String[] parts = source.split("E");
      source = parts[0] + ".0E" + parts[1];
    } else if (source.startsWith(".")) {
      source = "0" + source;
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
    if (negative) {
      result = -result;
    }
    return Real.make(result);
  }

  public static class Coercion extends ArcObject {
    private String name;

    Coercion(String name) {
      this.name = name;
    }

    public ArcObject type() {
      return Symbol.mkSym("fn");
    }

    public ArcObject coerce(ArcObject original) {
      throw new ArcError("not implemented: " + name + ".coerce 1 arg");
    }

    public ArcObject coerce(ArcObject original, ArcObject base) {
      throw new ArcError("not implemented: " + name + ".coerce 2 args");
    }

    public void invokef(VM vm, ArcObject arg) {
      vm.pushA(coerce(arg));
    }

    public void invokef(VM vm, ArcObject arg1, ArcObject arg2) {
      vm.pushA(coerce(arg1, arg2));
    }
  }

  private static ArcObject cantCoerce() {
    throw new CantCoerce();
  }

  public static class CantCoerce extends RuntimeException {
  }
}
