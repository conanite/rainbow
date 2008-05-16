package rainbow.functions;

import rainbow.ArcTestSupport;
import rainbow.parser.ParseException;
import rainbow.parser.ArcParser;
import rainbow.types.ArcObject;
import rainbow.types.Pair;
import rainbow.types.Symbol;
import junit.framework.Test;
import junit.framework.TestSuite;

import java.io.File;
import java.io.IOException;
import java.net.URL;

public class ArcTests {

  public static TestSuite suite() throws IOException, ParseException {
    ArcParser parser = new ArcParser(new File("tests/foundation.test"));
    Pair test = (Pair) parser.parseOneLine();
    return parseSuite(test);
  }

  private static TestSuite parseSuite(Pair test) {
    final TestSuite ts = new TestSuite(test.cdr().car().toString());
    Pair tests = (Pair) test.cdr().cdr();
    while (!tests.isNil()) {
      Pair c = (Pair) tests.car();
      tests = (Pair) tests.cdr();
      if (isSuite(c)) {
        ts.addTest(parseSuite(c));
      } else {
        ts.addTest(parseTest(c));
      }
    }
    return ts;
  }

  private static Test parseTest(Pair c) {
    String name = c.car().toString();
    ArcObject testExpression = c.cdr().car();
    String expected = c.cdr().cdr().car().toString();
    return new BuiltinTest(name, testExpression, expected);
  }

  private static boolean isSuite(Pair test) {
    return test.car() instanceof Symbol && ((Symbol) test.car()).name().equals("suite");
  }

  private static File read(String filename) throws IOException {
    URL in = ArcTests.class.getResource(filename);
    return new File(in.getFile());
  }

  static class BuiltinTest extends ArcTestSupport {
    private ArcObject testExpression;
    private String expected;

    BuiltinTest(String name, ArcObject testExpression, String expected) {
      setName(name);
      this.testExpression = testExpression;
      this.expected = expected;
    }

    protected void runTest() throws Throwable {
      String source = testExpression.toString();
      System.out.println(source);
      String result = vmEval(testExpression).toString();
      System.out.println(result);
      assertEquals(source, expected, result);
    }
  }
}
