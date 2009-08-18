package rainbow.types;

import rainbow.ArcError;
import rainbow.Function;
import rainbow.LexicalClosure;
import rainbow.vm.VM;

import java.util.*;

public class Hash extends LiteralObject {
  public static final Symbol TYPE = Symbol.mkSym("table");

  LinkedHashMap map = new LinkedHashMap();

  public void invoke(VM vm, Pair args) {
    vm.pushA(this.value(args.car()).or(args.cdr().car()));
  }

  public String toString() {
    return "#hash" + toList();
  }

  public Pair toList() {
    List pairs = new LinkedList();
    for (Iterator it = map.keySet().iterator(); it.hasNext();) {
      Object o = it.next();
      Pair keyValue = new Pair((ArcObject) o, new Pair((ArcObject) map.get(o), NIL));
      pairs.add(keyValue);
    }
    return Pair.buildFrom(pairs, EMPTY_LIST);
  }

  public long len() {
    return size();
  }

  public ArcObject sref(Pair args) {
    ArcObject value = args.car();
    ArcObject key = args.cdr().car();
    if (value.isNil()) {
      unref(key);
    } else {
      sref(key, value);
    }
    return value;
  }


  public void unref(ArcObject key) {
    map.remove(key);
  }

  public void sref(ArcObject key, ArcObject value) {
    map.put(key, value);
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

  public Object unwrap() {
    Map result = new HashMap();
    for (Iterator it = map.keySet().iterator(); it.hasNext();) {
      ArcObject key = (ArcObject) it.next();
      ArcObject value = (ArcObject) map.get(key);
      result.put(key.unwrap(), value.unwrap());
    }
    return result;
  }

  public long size() {
    return map.size();
  }

  public ArcObject map(Function f, LexicalClosure lc) {
    Pair kvs = toList();
    while (!kvs.isNil()) {
      Pair args = (Pair) kvs.car();
      f.invoke(lc, Pair.buildFrom(args.car(), args.cdr()));
      kvs = (Pair) kvs.cdr();
    }
    return this;
  }

  public static Hash cast(ArcObject argument, Object caller) {
    try {
      return (Hash) argument;
    } catch (ClassCastException e) {
      throw new ArcError("Wrong argument type: " + caller + " expected a hash, got " + argument + ", a " + argument.type());
    }
  }
}
