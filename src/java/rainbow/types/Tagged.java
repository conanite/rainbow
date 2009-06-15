package rainbow.types;

import rainbow.ArcError;
import rainbow.Function;
import rainbow.vm.ArcThread;
import rainbow.vm.continuations.TopLevelContinuation;

public class Tagged extends ArcObject {
  private ArcObject type;
  private ArcObject rep;
  private static final Symbol TAGGED_WRITE_FN = (Symbol) Symbol.make("tagged-writers");

  public Tagged(ArcObject type, ArcObject rep) {
    this.type = type;
    this.rep = rep;
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

    ArcThread thread = new ArcThread();
    TopLevelContinuation topLevel = new TopLevelContinuation(thread);
    Function f = (Function) fn;
    f.invoke(thread, null, topLevel, Pair.buildFrom(rep));
    thread.run();
    return (String) JavaObject.unwrap(thread.finalValue(), String.class);
  }


  public static Tagged cast(ArcObject argument, ArcObject caller) {
    try {
      return (Tagged) argument;
    } catch (ClassCastException e) {
      throw new ArcError("Wrong argument type: " + caller + " expected a Tagged, got " + argument);
    }
  }
}
