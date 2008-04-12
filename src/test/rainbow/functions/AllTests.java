package rainbow.functions;

import junit.framework.TestSuite;

import java.io.IOException;

import rainbow.types.*;
import rainbow.parser.ParseException;

public class AllTests extends TestSuite {

  public static TestSuite suite() throws IOException, ParseException {
    return new AllTests();
  }

  public AllTests() throws IOException, ParseException {
    addTest(ArcTests.suite());
    addTest(new TestSuite(RealTest.class));
    addTest(new TestSuite(ArcStringTest.class));
    addTest(new TestSuite(PairTest.class));
    addTest(new TestSuite(RationalTest.class));
  }
}
