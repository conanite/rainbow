package rainbow.types;

import rainbow.ArcError;
import rainbow.LexicalClosure;
import rainbow.vm.Continuation;

import java.util.HashMap;
import java.util.Map;

public class Symbol extends ArcObject {
  private static final Map<String, Symbol> map = new HashMap();
  public static final Symbol TYPE = (Symbol) Symbol.make("sym");
  public static final Symbol EMPTY_STRING = (Symbol) Symbol.make("||");
  public static final Symbol DOT = (Symbol) Symbol.make(".");
  public static final Symbol BANG = (Symbol) Symbol.make("!");
  private String name;
  private int hash;
  private ArcObject value;

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
    return (this == EMPTY_STRING) ? "" : name;
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

  public Object unwrap() {
    return name();
  }

  public int hashCode() {
    return hash;
  }

  public boolean equals(Object object) {
    return this == object;
  }

  public void interpret(LexicalClosure lc, Continuation caller) {
    try {
      caller.receive(value());
    } catch (Unbound e) {
      caller.error(e);
    }
  }

  public static boolean is(String s, ArcObject o) {
    return (o instanceof Symbol) && ((Symbol)o).name().equals(s);
  }

  public void setSymbolValue(LexicalClosure lc, ArcObject value) {
    this.value = value;
  }

  public void setValue(ArcObject value) {
    this.value = value;
  }

  public ArcObject value() {
    if (value == null) {
      throw new Unbound(this);
    }
    return value;
  }

  public boolean bound() {
    return value != null;
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
