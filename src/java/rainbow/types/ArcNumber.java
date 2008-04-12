package rainbow.types;

import rainbow.types.ArcObject;
import rainbow.Bindings;

public abstract class ArcNumber extends ArcObject {
  public static final Symbol INT_TYPE = (Symbol) Symbol.make("int");
  public static final Symbol NUM_TYPE = (Symbol) Symbol.make("num");

  public ArcObject eval(Bindings arc) {
    return this;
  }

  public abstract boolean isInteger();

  public abstract double toDouble();

  public abstract long toInt();

  public abstract ArcNumber negate();

  public int compareTo(ArcObject right) {
    double comparison = ((ArcNumber) right).toDouble() - this.toDouble();
    return comparison < 0 ? 1 : comparison == 0 ? 0 : -1;
  }

  public ArcObject type() {
    return isInteger() ? INT_TYPE : NUM_TYPE;
  }
}
