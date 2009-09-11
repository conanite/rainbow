package rainbow.types;

import rainbow.ArcError;
import rainbow.vm.instructions.FreeSym;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Symbol extends ArcObject {
  private static final Map<String, Symbol> map = new HashMap();
  public static final Symbol TYPE = Symbol.mkSym("sym");
  public static final Symbol EMPTY_STRING = Symbol.mkSym("||");
  public static final Symbol DOT = Symbol.mkSym(".");
  public static final Symbol BANG = Symbol.mkSym("!");
  private String name;
  private int hash;
  private ArcObject value;
  private Map coerceFrom;

  protected Symbol(String name) {
    this.name = name;
    this.hash = name.hashCode();
  }

  public void addInstructions(List i) {
    i.add(new FreeSym(this));
  }

  public static Symbol mkSym(String name) {
    if ("t".equals(name)) {
      return T;
    } else if ("nil".equals(name)) {
      throw new ArcError("can't make symbol 'nil");
    }
    return nu(name);
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

  public static boolean is(String s, ArcObject o) {
    return (o instanceof Symbol) && ((Symbol) o).name().equals(s);
  }

  public void setValue(ArcObject value) {
    ArcObject old = this.value;
    this.value = value;
    if (old != null) {
      old.unassigned(this);
    }
    value.assigned(this);
  }

  public ArcObject value() {
    if (value == null) {
      throw new ArcError("Symbol " + name + " is not bound");
    }
    return value;
  }

  public boolean bound() {
    return value != null;
  }

  public static Symbol cast(ArcObject argument, Object caller) {
    try {
      return (Symbol) argument;
    } catch (ClassCastException e) {
      throw new ArcError("Wrong argument type: " + caller + " expected a Symbol, got " + argument);
    }
  }

  public void addCoercion(Symbol from, ArcObject function) {
    if (coerceFrom == null) {
      coerceFrom = new HashMap();
    }
    coerceFrom.put(from, function);
  }

  public ArcObject getCoercion(Symbol from) {
    return (ArcObject) coerceFrom.get(from);
  }
}
