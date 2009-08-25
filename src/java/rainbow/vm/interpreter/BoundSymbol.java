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
  final Symbol name;

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
    return "BS:" + name + "[nesting:" + nesting + "]";
//    return name.name();
  }

  public boolean isSameBoundSymbol(BoundSymbol other) {
    return nesting == other.nesting && name == other.name && index == other.index;
  }

  public int highestLexicalScopeReference() {
    return nesting;
  }

  public BoundSymbol nest() {
    return new BoundSymbol(name, nesting + 1, index);
  }

  public ArcObject inline(BoundSymbol p, ArcObject arg, boolean unnest) {
    if (this.isSameBoundSymbol(p)) {
      return arg;
    } else if (unnest) {
      return new BoundSymbol(name, nesting - 1, index);
    } else {
      return this;
    }
  }
}
