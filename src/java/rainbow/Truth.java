package rainbow;

import rainbow.types.ArcObject;
import rainbow.types.Symbol;

public class Truth extends Symbol {
  public static final Truth T = new Truth();

  private Truth() {
    super("t");
  }

  public boolean literal() {
    return true;
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

  public Object unwrap() {
    return Boolean.TRUE;
  }

  public void setSymbolValue(LexicalClosure lc, ArcObject value) {
    throw new ArcError("error: can't rebind t!");
  }

  public void setValue(ArcObject value) {
    throw new ArcError("error: can't rebind t!");
  }

  public ArcObject value() {
    return this;
  }
}
