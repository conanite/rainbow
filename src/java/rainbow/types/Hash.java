package rainbow.types;

import rainbow.Function;
import rainbow.LexicalClosure;
import rainbow.vm.ArcThread;
import rainbow.vm.Continuation;
import rainbow.vm.continuations.TableMapper;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;

public class Hash extends ArcObject implements Function {
  public static final Symbol TYPE = (Symbol) Symbol.make("table");
  LinkedHashMap map = new LinkedHashMap();

  public String toString() {
    return "#hash" + toList();
  }

  public Pair toList() {
    List pairs = new LinkedList();
    for (Iterator it = map.keySet().iterator(); it.hasNext();) {
      Object o = it.next();
      Pair keyValue = new Pair((ArcObject) o, (ArcObject) map.get(o));
      pairs.add(keyValue);
    }
    return Pair.buildFrom(pairs, NIL);
  }

  public void sref(ArcObject key, ArcObject value) {
    map.put(key, value);
  }

  public void invoke(ArcThread thread, LexicalClosure lc, Continuation whatToDo, Pair args) {
    whatToDo.receive(value(args.car()));
  }

  public String code() {
    return "<hash>";
  }

  private ArcObject value(ArcObject key) {
    ArcObject result = (ArcObject) map.get(key);
    return result == null ? NIL : result;
  }

  public int compareTo(ArcObject right) {
    return 0;
  }

  public ArcObject type() {
    return TYPE;
  }

  public long size() {
    return map.size();
  }

  public void map(Function f, ArcThread thread, LexicalClosure lc, Continuation whatToDo) {
    new TableMapper(thread, lc, whatToDo, f, this).receive(null);
  }
}
