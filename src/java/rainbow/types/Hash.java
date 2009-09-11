package rainbow.types;

import rainbow.ArcError;
import rainbow.Nil;
import rainbow.vm.VM;

import java.util.*;

public class Hash extends LiteralObject {
  public static final Symbol TYPE = Symbol.mkSym("table");

  private ArcObject name = NIL;
  LinkedHashMap map = new LinkedHashMap();
  private Naming naming = new DontName();

  public void unassigned(ArcObject name) {
    if (this.name == name) {
      this.name = NIL;
      naming = new DontName();
    }
  }

  public void assigned(ArcObject name) {
    this.name = name;
    naming = new DoName();
  }

  public ArcObject assignedName() {
    return name;
  }

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
    return (Pair)Pair.buildFrom(pairs, EMPTY_LIST);
  }

  public long len() {
    return size();
  }

  public ArcObject sref(ArcObject value, ArcObject key) {
    if (value instanceof Nil) {
      ArcObject previous = value(key);
      unref(key);
      naming.unname(previous, key);
    } else {
      map.put(key, value);
      naming.name(value, key);
    }
    return value;
  }

  public void unref(ArcObject key) {
    map.remove(key);
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

  public static Hash cast(ArcObject argument, Object caller) {
    try {
      return (Hash) argument;
    } catch (ClassCastException e) {
      throw new ArcError("Wrong argument type: " + caller + " expected a hash, got " + argument + ", a " + argument.type());
    }
  }

  interface Naming {
    void name(ArcObject o, ArcObject name);
    void unname(ArcObject o, ArcObject name);
  }

  class DoName implements Naming {
    public void name(ArcObject o, ArcObject name) {
      o.assigned(new Pair(name, Hash.this.name));
    }

    public void unname(ArcObject o, ArcObject name) {
      o.unassigned(new Pair(name, Hash.this.name));
    }
  }

  class DontName implements Naming {
    public void name(ArcObject o, ArcObject name) { }
    public void unname(ArcObject o, ArcObject name) { }
  }
}
