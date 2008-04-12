package rainbow.types;

import junit.framework.TestCase;

public class RealTest extends TestCase {
  public void testEvalReturnsSelf() {
    ArcNumber n = new Real(123.45);
    assertSame(n, n.eval(null));
  }
}
