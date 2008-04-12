package rainbow;

import rainbow.types.ArcObject;
import rainbow.types.Symbol;

public class Truth extends Symbol {
  public static final Truth T = new Truth();

  private Truth() {
    super("t");
  }

  public ArcObject eval(Bindings arc) {
    return this;
  }

  public String toString() {
    return "t";
  }

  public int compareTo(ArcObject right) {
    throw new ArcError("Truth.compareTo:unimplemented");
  }

  public ArcObject type() {
    return Symbol.TYPE;
  }

  public static ArcObject valueOf(boolean b) {
    return b ? T : NIL;
  }

  public int hashCode() {
    return "t".hashCode();
  }

  public boolean equals(Object object) {
    return this == object;
  }

  public static boolean isIn(ArcObject arcObject) {
    return !arcObject.isNil();
  }
}