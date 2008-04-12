package rainbow.types;

import rainbow.types.ArcObject;
import rainbow.*;

import java.util.Map;
import java.util.HashMap;

public class Symbol extends ArcObject {
  public static final Symbol TYPE = (Symbol) Symbol.make("sym");
  public static final Symbol EMPTY_STRING = (Symbol) Symbol.make("||");
  private static final Map<String, Symbol> map = new HashMap();
  private String name;

  public Symbol(String name) {
    this.name = name;
  }

  public static ArcObject make(String name) {
    if ("t".equals(name)) {
      return ArcObject.T;
    } else if ("nil".equals(name)) {
      return ArcObject.NIL;
    }
    return new Symbol(name);
  }

  public String toString() {
    return name;
  }

  public static Symbol nu(String s) {
    s = s.intern();
    if (map.containsKey(s)) {
      return map.get(s);
    }

    Symbol result = new Symbol(s);
    map.put(s, result);
    return result;
  }

  public String name() {
    return name;
  }

  public int compareTo(ArcObject right) {
    return name.compareTo(((Symbol) right).name);
  }

  public ArcObject type() {
    return TYPE;
  }

  public ArcObject eqv(ArcObject other) {
    return Truth.valueOf(equals(other));
  }

  public int hashCode() {
    return name.hashCode();
  }

  public boolean equals(Object object) {
    return object instanceof Symbol && ((Symbol) object).name.equals(this.name);
  }

  public ArcObject eval(Bindings arc) {
    ArcObject result = arc.lookup(name);
    if (result == null) {
      throw new Unbound(this);
    }
    return result;
  }

  public static boolean is(String s, ArcObject o) {
    return (o instanceof Symbol) && ((Symbol)o).name().equals(s);
  }

  private class Unbound extends ArcError {
    public Unbound(Symbol name) {
      super("Symbol " + name + " is not bound");
    }
  }
}
