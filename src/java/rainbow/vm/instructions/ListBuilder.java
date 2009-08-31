package rainbow.vm.instructions;

import rainbow.types.ArcObject;
import rainbow.types.Pair;
import rainbow.types.Symbol;

import java.util.List;
import java.util.ArrayList;

public class ListBuilder extends ArcObject {
  private final List list = new ArrayList();
  private ArcObject last = NIL;

  public void append(ArcObject o) {
    list.add(o);
  }

  public void appendAll(Pair p) {
    p.copyTo(list);
  }

  public void last(ArcObject o) {
    last = o;
  }

  public ArcObject list() {
    return Pair.buildFrom(list, last);
  }

  public ArcObject type() {
    return Symbol.mkSym("list-builder");
  }

  public String toString() {
    return "ListBuilder:" + list().toString();
  }
}
