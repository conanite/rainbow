package rainbow.types;

import rainbow.Function;
import rainbow.LexicalClosure;
import rainbow.ArcError;
import rainbow.vm.ArcThread;
import rainbow.vm.Continuation;
import rainbow.vm.continuations.TableMapper;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;

public class Hash extends ArcObject {
  public static final Symbol TYPE = (Symbol) Symbol.make("table");

  public static final Function REF = new Function() {
    public void invoke(ArcThread thread, LexicalClosure lc, Continuation whatToDo, Pair args) {
      Hash hash = Hash.cast(args.car(), this);
      whatToDo.receive(hash.value(args.cdr().car()));
    }

    public String code() {
      return "pair-ref";
    }
  };


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

  public String code() {
    return "<hash>";
  }

  public ArcObject value(ArcObject key) {
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
  
  public static Hash cast(ArcObject argument, Object caller) {
    try {
      return (Hash) argument;
    } catch (ClassCastException e) {
      throw new ArcError("Wrong argument type: " + caller + " expected a hash, got " + argument + ", a " + argument.type());
    }
  }
}
