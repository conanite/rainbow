package rainbow.types;

import rainbow.Bindings;
import rainbow.Function;
import rainbow.vm.ArcThread;
import rainbow.vm.Continuation;
import rainbow.vm.continuations.TableMapper;

import java.util.*;

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

  public void invoke(ArcThread thread, Bindings namespace, Continuation whatToDo, Pair args) {
    whatToDo.eat(value(args.car()));
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

  public void map(Function f, ArcThread thread, Bindings namespace, Continuation whatToDo) {
    new TableMapper(thread, namespace, whatToDo, f, this).eat(null);
  }
}
