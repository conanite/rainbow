package rainbow.types;

import junit.framework.TestCase;
import rainbow.types.ArcString;

public class ArcStringTest extends TestCase {

  public void testEvalReturnsSelf() {
    ArcString s = new ArcString("foo");
    assertSame(s, s.eval(null));
  }
}
