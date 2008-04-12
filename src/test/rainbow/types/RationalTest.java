package rainbow.types;

import junit.framework.TestCase;

public class RationalTest extends TestCase {

  public void testEvalReturnsSelf() {
    ArcNumber n = new Rational(123, 45);
    assertSame(n, n.eval(null));
  }

  public void testMultiplyFractions() {
    Rational a = new Rational(9, 4);
    Rational b = new Rational(4, 3);
    assertEquals(3.0, a.times(b).toDouble());
  }
}
