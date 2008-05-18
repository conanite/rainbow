package rainbow.types;

import rainbow.types.ArcObject;
import rainbow.*;

import java.util.Map;
import java.util.HashMap;

public class Symbol extends ArcObject {
  private static final Map<String, Symbol> map = new HashMap();
  public static final Symbol TYPE = (Symbol) Symbol.make("sym");
  public static final Symbol EMPTY_STRING = (Symbol) Symbol.make("||");
  private static int count = 0;
  private String name;
  private int hash;

  protected Symbol(String name) {
    this.name = name;
    this.hash = name.hashCode();
  }

  public static ArcObject make(String name) {
    if ("t".equals(name)) {
      return T;
    } else if ("nil".equals(name)) {
      return NIL;
    }
    return nu(name);
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
    return hash;
  }

  public boolean equals(Object object) {
    return this == object;
  }

  public ArcObject eval(Environment env) {
    ArcObject result = env.lookup(this);
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

  public static Symbol cast(ArcObject argument, Object caller) {
    try {
      return (Symbol) argument;
    } catch (ClassCastException e) {
      throw new ArcError("Wrong argument type: " + caller + " expected a Symbol, got " + argument);
    }
  }
}
