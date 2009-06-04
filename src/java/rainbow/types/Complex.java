package rainbow.types;

import rainbow.ArcError;
import rainbow.parser.ComplexParser;
import rainbow.parser.ParseException;

public class Complex extends ArcNumber {
  public static final Complex ZERO = new Complex(0, 0);

  private double real;
  private double imaginary;

  public Complex(double real, double imaginary) {
    this.real = real;
    this.imaginary = imaginary;
  }

  public Complex(ArcNumber real, ArcNumber imaginary) {
    this.real = real.toDouble();
    this.imaginary = imaginary.toDouble();
  }

  public static Complex parse(String number) throws ParseException {
    return new ComplexParser(number).complex();
  }

  public boolean isInteger() {
    return imaginary == 0.0 && Math.floor(real) == real;
  }

  public double toDouble() {
    throw new ArcError("Cannot convert complex to double");
  }

  public long toInt() {
    throw new ArcError("Cannot convert complex to int");
  }

  public ArcNumber negate() {
    return new Complex(-real, -imaginary);
  }

  public String toString() {
    return new Real(real).toString() + (imaginary < 0 ? "" : "+") + new Real(imaginary).toString() + "i";
  }

  public static Complex cast(ArcObject argument, Object caller) {
    try {
      return (Complex) argument;
    } catch (ClassCastException e) {
      throw new ArcError("Wrong argument type: " + caller + " expected a complex number, got " + argument);
    }
  }

  @Override
  public ArcObject round() {
    throw new ArcError("Can't convert " + this + " to integer");
  }

  public ArcNumber imaginaryPart() {
    return new Real(imaginary);
  }

  public ArcNumber realPart() {
    return new Real(real);
  }

  public Complex times(Complex c) {
    double r1 = this.real;
    double r2 = c.real;
    double i1 = this.imaginary;
    double i2 = c.imaginary;

    return new Complex(r1 * r2 - i1 * i2, r1 * i2 + r2 * i1);
  }

  public Complex plus(Complex other) {
    return new Complex(this.real + other.real, this.imaginary + other.imaginary);
  }

  public Complex inverse() {
    // http://en.wikipedia.org/wiki/Complex_numbers#The_field_of_complex_numbers
    // for a + bi
    // real part: a / (a^2 + b^2)
    // imaginary part: -b / (a^2 + b^2)

    double denominator = real * real + imaginary * imaginary;
    double r = real / denominator;
    double i = (-imaginary) / denominator;
    return new Complex(r, i);
  }

  public Complex times(double scalar) {
    double r = this.real * scalar;
    double i = this.imaginary * scalar;

    return new Complex(r, i);
  }

  public double radius() {
    return Math.sqrt(real * real + imaginary * imaginary);
  }

  public double theta() {
    return Math.atan2(imaginary, real);
  }

  public Complex log() {
    return new Complex(Math.log(radius()), theta());
  }

  public ArcNumber expt(ArcNumber exponent) {
    // http://en.wikipedia.org/wiki/Exponentiation#Complex_power_of_a_complex_number
    // a^b = e^(b log a)

    if (exponent instanceof Complex) {
      return this.log().times((Complex)exponent).exp();
    } else {
      return this.log().times(exponent.toDouble()).exp();
    }
  }

  private Complex exp() {
    // http://en.wikipedia.org/wiki/Exponentiation#Imaginary_powers_of_e
    // e^(x+iy) = e^x . e^iy = e^x . (cos y + i.sin y)

    double e_x = Math.exp(real);
    return new Complex(e_x * Math.cos(imaginary), e_x * Math.sin(imaginary));
  }

  public int compareTo(ArcObject right) {
    throw new ArcError("Compare: complex numbers are unordered and cannot be compared");
  }

  public static Complex make(ArcObject o) {
    if (o instanceof Complex) {
      return (Complex) o;
    } else if (o instanceof ArcNumber) {
      return new Complex(((ArcNumber)o).toDouble(), 0);
    } else {
      throw new ArcError("Can't make complex number from " + o);
    }
  }

  public boolean isSame(ArcObject other) {
    if (other instanceof Complex) {
      return ((Complex)other).real == this.real && ((Complex)other).imaginary == this.imaginary;
    } else {
      return other instanceof ArcNumber && imaginary == 0.0 && ((ArcNumber) other).toDouble() == this.real;
    }
  }
}
