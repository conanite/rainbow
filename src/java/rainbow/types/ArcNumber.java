package rainbow.types;

import rainbow.ArcError;

public abstract class ArcNumber extends LiteralObject {
  public static final Symbol INT_TYPE = Symbol.mkSym("int");
  public static final Symbol NUM_TYPE = Symbol.mkSym("num");

  public abstract boolean isInteger();

  public abstract double toDouble();

  public abstract long toInt();

  public abstract ArcNumber negate();

  public abstract ArcObject round();

  public ArcObject roundJava() {
    throw new ArcError("round[closest] not implemented for " + this.getClass() + "(" + this + ")");
  }

  public boolean literal() {
    return true;
  }

  public int compareTo(ArcObject right) {
    double comparison = ((ArcNumber) right).toDouble() - this.toDouble();
    return comparison < 0 ? 1 : comparison == 0 ? 0 : -1;
  }

  public ArcObject type() {
    return isInteger() ? INT_TYPE : NUM_TYPE;
  }

  public boolean isSame(ArcObject other) {
    return equals(other);
  }

  public static ArcNumber cast(ArcObject argument, Object caller) {
    try {
      return (ArcNumber) argument;
    } catch (ClassCastException e) {
      throw new ArcError("Wrong argument type: " + caller + " expected a number, got " + argument);
    }
  }

  public ArcObject mod(ArcNumber other) {
    throw new ArcError("mod: not implemented for " + this.getClass() + "(" + this + ")");
  }
}
