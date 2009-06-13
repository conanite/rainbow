package rainbow.types;

import rainbow.ArcError;
import rainbow.Function;
import rainbow.LexicalClosure;
import rainbow.functions.Builtin;
import rainbow.vm.ArcThread;
import rainbow.vm.Continuation;

public class ArcString extends ArcObject {
  public static final Symbol TYPE = (Symbol) Symbol.make("string");

  public static final Function REF = new Function() {
    public void invoke(ArcThread thread, LexicalClosure lc, Continuation caller, Pair args) {
      Builtin.checkMaxArgCount(args, getClass(), 2);
      ArcString string = ArcString.cast(args.car(), this);
      Rational index = Rational.cast(args.cdr().car(), this);
      if (!index.isInteger()) {
        throw new ArcError("string-ref: expects exact integer: got " + index);
      }
      int i = (int) index.toInt();
      if (i < 0 || i >= string.value.length()) {
        throw new ArcError("string-ref: index " + i + " out of range [0, " + (string.value.length() - 1) + "] for string " + toString());
      }
      caller.receive(new ArcCharacter(string.value.charAt(i)));
    }

    public String toString() {
      return "string-ref";
    }
  };

  private String value;

  private ArcString(String value) {
    this.value = value;
  }

  public String value() {
    return value;
  }

  public String toString() {
    return escape(value);
  }

  private String escape(String value) {
    StringBuffer sb = new StringBuffer();
    sb.append("\"");
    String v = value;
    for (int i = 0; i < v.length(); i++) {
      switch (v.charAt(i)) {
        case '"'  : sb.append("\\\""); break;
        case '\\' : sb.append("\\\\"); break;
        case '\n' : sb.append("\\n"); break;
        case '\r' : sb.append("\\r"); break;
        default   : sb.append(v.charAt(i));
      }
    }
    sb.append("\"");
    return sb.toString();
  }

  public int compareTo(ArcObject right) {
    return value.compareTo(((ArcString) right).value);
  }

  public long len() {
    return value.length();
  }

  public Function refFn() {
    return ArcString.REF;
  }

  public ArcObject scar(ArcObject character) {
    ArcCharacter newCar = ArcCharacter.cast(character, this);
    StringBuilder sb = new StringBuilder(newCar.stringValue());
    sb.append(value().substring(1));
    setValue(sb.toString());
    return character;
  }

  public ArcObject sref(Pair args) {
    ArcCharacter value = ArcCharacter.cast(args.car(), ArcCharacter.class);
    Rational index = Rational.cast(args.cdr().car(), this);
    sref(index, value);
    return value;
  }

  public ArcObject type() {
    return TYPE;
  }

  public Object unwrap() {
    return value();
  }

  public static ArcString make(String element) {
    return new ArcString(element);
  }

  public void setValue(String s) {
    this.value = s;
  }

  public int hashCode() {
    return value.hashCode();
  }

  public boolean equals(Object object) {
    return this == object || object instanceof ArcString && ((ArcString) object).value.equals(this.value);
  }

  public void sref(Rational index, ArcCharacter value) {
    StringBuilder b = new StringBuilder(this.value);
    b.setCharAt((int) index.toInt(), value.value());
    this.value = b.toString();
  }

  public boolean isSame(ArcObject other) {
    return equals(other);
  }

  public static ArcString cast(ArcObject argument, Object caller) {
    try {
      return (ArcString) argument;
    } catch (ClassCastException e) {
      throw new ArcError("Wrong argument type: " + caller + " expected a string, got " + argument);
    }
  }
}
