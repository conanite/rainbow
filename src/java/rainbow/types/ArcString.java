package rainbow.types;

import rainbow.types.ArcObject;
import rainbow.*;
import rainbow.vm.ArcThread;
import rainbow.vm.Continuation;
import rainbow.functions.Builtin;

public class ArcString extends ArcObject implements Function {
  public static final Symbol TYPE = (Symbol) Symbol.make("string");
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

  public ArcObject eval(Bindings arc) {
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

  public void invoke(ArcThread thread, Bindings namespace, Continuation whatToDo, Pair args) {
    Builtin.checkMaxArgCount(args, getClass(), 1);
    Rational index = cast(args.car(), Rational.class);
    if (!index.isInteger()) {
      throw new ArcError("string-ref: expects exact integer: got " + index);
    }
    int i = (int) index.toInt();
    if (i < 0 || i >= value.length()) {
      throw new ArcError("string-ref: index " + i + " out of range [0, " + (value.length() - 1) + "] for string " + toString());
    }
    whatToDo.eat(new ArcCharacter(value.charAt(i)));
  }

  public String code() {
    return "<string>";
  }
}
