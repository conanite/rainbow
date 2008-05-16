package rainbow.types;

import junit.framework.TestCase;

import java.util.List;
import java.util.ArrayList;

import rainbow.types.ArcNumber;
import rainbow.types.ArcObject;
import rainbow.types.ArcString;
import rainbow.types.Pair;

public class PairTest extends TestCase {
  private ArcObject bar = new ArcString("barbar");
  private boolean fooWasCalled = false;

  public void testCreatesListOfLinkedConses() {
    List list = new ArrayList();
    ArcString foo = new ArcString("foo");
    Symbol barId = (Symbol) Symbol.make("bar");
    ArcNumber num = new Real(123.45);
    list.add(foo);
    list.add(barId);
    list.add(num);
    Pair pair = Pair.buildFrom(list, ArcObject.NIL);
    assertEquals(3, pair.size());
    assertSame(foo, pair.car());
    assertSame(barId, pair.cdr().car());
    assertSame(num, pair.cdr().cdr().car());
    assertSame(ArcObject.NIL, pair.cdr().cdr().cdr());
  }
}
