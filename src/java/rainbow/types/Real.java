package rainbow.types;

import java.text.DecimalFormat;

public class Real extends ArcNumber {
  private double value;

  public Real(double value) {
    this.value = value;
  }

  public static Real positiveInfinity() {
    return new Real(Double.POSITIVE_INFINITY);
  }

  public static Real negativeInfinity() {
    return new Real(Double.NEGATIVE_INFINITY);
  }

  public static Real nan() {
    return new Real(Double.NaN);
  }

  public static Real parse(String rep) {
    return make(Double.parseDouble(rep));
  }

  public static Real make(double v) {
    return new Real(v);
  }

  public String toString() {
    if (Double.isInfinite(value)) {
      if (value < 0) {
        return "-inf.0";
      } else {
        return "+inf.0";
      }
    } else if (Double.isNaN(value)) {
      return "+nan.0";
    } else {
      return new DecimalFormat("0.0##############").format(value);
    }
  }

  public Object unwrap() {
    if (isInteger()) {
      return (long) value();
    } else {
      return value();
    }
  }

  public ArcObject type() {
    return NUM_TYPE;
  }

  public double value() {
    return value;
  }

  public boolean isInteger() {
    return Math.floor(value) == value;
  }

  public double toDouble() {
    return value;
  }

  public long toInt() {
    return (long) value;
  }

  public ArcNumber negate() {
    return make(-value);
  }

  public int hashCode() {
    return new Double(value).hashCode();
  }

  public boolean equals(Object other) {
    if (other instanceof Complex) {
      return ((Complex)other).isSame(this);
    }
    return (this == other) || (other instanceof Real && value == ((Real) other).value);
  }

  public ArcObject round() {
    return Rational.make(Math.round(value));
  }
}
