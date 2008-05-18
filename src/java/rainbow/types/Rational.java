package rainbow.types;

import rainbow.ArcError;
import rainbow.Environment;
import rainbow.Truth;

public class Rational extends ArcNumber {
  private long numerator;
  private long denominator;
  public static final Rational ZERO = make(0);
  public static final Rational ONE = make(1);
  public static final Rational TEN = make(10);

  public Rational(long numerator, long denominator) {
    if (denominator == 0 && numerator != 0) {
      throw new ArcError("/: division by zero");
    }
    long gcd = gcd(numerator, denominator);
    this.numerator = numerator / gcd;
    this.denominator = denominator / gcd;
  }

  public static Rational parse(String rep) {
    String[] parts = rep.split("/");
    return make(Long.parseLong(parts[0]), Long.parseLong(parts[1]));
  }

  public static Rational make(long result) {
    return make(result, 1);
  }

  public static Rational make(long a, long b) {
    return new Rational(a, b);
  }

  public String toString() {
    return numerator + (denominator == 1 ? "" : "/" + denominator);
  }

  public ArcObject eval(Environment env) {
    return this;
  }

  public boolean isInteger() {
    return denominator == 1;
  }

  public double toDouble() {
    return (double) numerator / (double) denominator;
  }

  public long toInt() {
    if (numerator == 0) {
      return 0;
    } else {
      return numerator / denominator;
    }
  }

  private long gcd(long a, long b) {
    if (b == 0) {
      return a;
    }
    return gcd(b, a % b);
  }

  public Rational times(Rational other) {
    return new Rational(numerator * other.numerator, denominator * other.denominator);
  }

  public Rational plus(Rational other) {
    long num = (this.numerator * other.denominator) + (other.numerator * this.denominator);
    long div = this.denominator * other.denominator;
    return make(num, div);
  }

  public Rational negate() {
    return make(-numerator, denominator);
  }

  public Rational invert() {
    return new Rational(denominator, numerator);
  }

  public ArcObject eqv(ArcObject other) {
    return Truth.valueOf(this == other || (equals(other)));
  }

  public int hashCode() {
    return (int) ((37 * numerator) + denominator);
  }

  public boolean equals(Object object) {
    return object instanceof Rational && sameValue((Rational) object);
  }

  private boolean sameValue(Rational other) {
    return this.numerator == other.numerator && this.denominator == other.denominator;
  }

  public long numerator() {
    return numerator;
  }

  public long denominator() {
    return denominator;
  }
  
  public static Rational cast(ArcObject argument, Object caller) {
    try {
      return (Rational) argument;
    } catch (ClassCastException e) {
      throw new ArcError("Wrong argument type: " + caller + " expected a Rational, got " + argument);
    }
  }
}
