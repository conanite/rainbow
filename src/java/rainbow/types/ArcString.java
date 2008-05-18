package rainbow.types;

import rainbow.types.ArcObject;
import rainbow.*;
import rainbow.vm.ArcThread;
import rainbow.vm.Continuation;
import rainbow.functions.Builtin;

public class ArcString extends ArcObject {
  public static final Symbol TYPE = (Symbol) Symbol.make("string");

  public static final Function REF = new Function() {
    public void invoke(ArcThread thread, LexicalClosure lc, Continuation whatToDo, Pair args) {
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
      whatToDo.receive(new ArcCharacter(string.value.charAt(i)));
    }

    public String code() {
      return "string-ref";
    }
  };

  private String value;

  public ArcString(String value) {
    this.value = value;
  }

  public String value() {
    return value;
  }

  public String toString() {
    return "\"" + value + "\"";
  }

  public ArcObject eval(Environment env) {
    return this;
  }

  public int compareTo(ArcObject right) {
    return value.compareTo(((ArcString) right).value);
  }

  public ArcObject eqv(ArcObject other) {
    return Truth.valueOf(equals(other));
  }

  public ArcObject type() {
    return TYPE;
  }

  public static ArcString make(String element) {
    return new ArcString(element);
  }

  public static ArcString parse(String input) {
    return make(input.replaceAll("\\\\\"", "\"").replaceAll("\\\\n", "\n"));
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

  public String code() {
    return "<string>";
  }
  
  public static ArcString cast(ArcObject argument, Object caller) {
    try {
      return (ArcString) argument;
    } catch (ClassCastException e) {
      throw new ArcError("Wrong argument type: " + caller + " expected a string, got " + argument);
    }
  }
}
