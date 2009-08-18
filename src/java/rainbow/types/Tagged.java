package rainbow.types;

import rainbow.ArcError;
import rainbow.vm.VM;

public class Tagged extends LiteralObject {
  private ArcObject type;
  private ArcObject rep;
  private static final Symbol TAGGED_WRITE_FN = Symbol.mkSym("tagged-writers");

  public Tagged(ArcObject type, ArcObject rep) {
    this.type = type;
    this.rep = rep;
  }

  public void invoke(VM vm, Pair args) {
    ((Hash) TYPE_DISPATCHER_TABLE.value()).value(type).invoke(vm, new Pair(rep, args));
  }

  public ArcObject getType() {
    return type;
  }

  public ArcObject getRep() {
    return rep;
  }

  public int compareTo(ArcObject right) {
    return 0;
  }

  public ArcObject type() {
    return type;
  }

  public static boolean hasTag(ArcObject o, String s) {
    return o instanceof Tagged && ((Tagged)o).getType().toString().equals(s);
  }

  public static ArcObject ifTagged(ArcObject o, String tag) {
    if (hasTag(o, tag)) {
      return ((Tagged)o).getRep();
    }
    return null;
  }

  public static ArcObject rep(ArcObject o) {
    return (o instanceof Tagged) ? ((Tagged)o).rep : o;
  }

  public String toString() {
    return stringify();
  }

  public String defaultToString() {
    return "#<tagged " + type + " " + rep + ">";
  }

  private String stringify() {
    Symbol writer = Tagged.TAGGED_WRITE_FN;
    if (!writer.bound()) {
      return defaultToString();
    }

    Hash dispatchers = (Hash) writer.value();
    ArcObject fn = dispatchers.value(type);
    if (fn.isNil()) {
      return defaultToString();
    }

    VM vm = new VM();
    fn.invoke(vm, Pair.buildFrom(rep));
    return (String) JavaObject.unwrap(vm.thread(), String.class);
  }


  public static Tagged cast(ArcObject argument, ArcObject caller) {
    try {
      return (Tagged) argument;
    } catch (ClassCastException e) {
      throw new ArcError("Wrong argument type: " + caller + " expected a Tagged, got " + argument);
    }
  }
}
