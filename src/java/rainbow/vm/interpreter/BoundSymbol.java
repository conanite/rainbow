package rainbow.vm.interpreter;

import rainbow.LexicalClosure;
import rainbow.types.ArcObject;
import rainbow.types.Symbol;
import rainbow.vm.instructions.LexSym;

import java.util.List;

public class BoundSymbol extends ArcObject {
  public static final Symbol TYPE = Symbol.mkSym("bound-symbol");
  private final int nesting;
  private final int index;
  private final Symbol name;

  public BoundSymbol(Symbol name, int nesting, int index) {
    this.nesting = nesting;
    this.index = index;
    this.name = name;
  }

  public void setSymbolValue(LexicalClosure lc, ArcObject value) {
    lc.nth(nesting).set(index, value);
  }

  public ArcObject interpret(LexicalClosure lc) {
    return lc.nth(nesting).at(index);
  }

  public void addInstructions(List i) {
    i.add(new LexSym(this));
  }

  public ArcObject type() {
    return TYPE;
  }

  public String toString() {
    return name.name();
  }

  public boolean isSameBoundSymbol(BoundSymbol other) {
    return nesting == other.nesting && name == other.name && index == other.index;
  }
}
