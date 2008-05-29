package rainbow.vm;

import rainbow.types.ArcObject;
import rainbow.types.Symbol;
import rainbow.LexicalClosure;

public class BoundSymbol extends ArcObject {
  public static final Symbol TYPE = (Symbol) Symbol.make("bound-symbol");
  private final int nesting;
  private final int index;
  private final Symbol name;

  public BoundSymbol(Symbol name, int nesting, int index) {
    this.nesting = nesting;
    this.index = index;
    this.name = name;
  }
  
  public ArcObject lookup(LexicalClosure lc) {
    return lc.nth(nesting).at(index);
  }

  public ArcObject type() {
    return TYPE;
  }

  public void set(LexicalClosure lc, ArcObject o) {
    lc.nth(nesting).set(index, o);
  }
  
  public String toString() {
    return name.name();
  }
}
