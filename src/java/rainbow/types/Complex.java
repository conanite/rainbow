package rainbow.types;

public class Complex extends ArcNumber {
  private ArcNumber real;
  private ArcNumber imaginary;

  public boolean isInteger() {
    return imaginary.toDouble() == 0.0 && real.isInteger();
  }

  public double toDouble() {
    return 0;
  }

  public long toInt() {
    return 0;
  }

  public ArcNumber negate() {
    return null;
  }
}
