package rainbow.vm.interpreter;

import rainbow.ArcError;
import rainbow.LexicalClosure;
import rainbow.types.ArcObject;
import rainbow.types.Symbol;
import rainbow.vm.instructions.LexSym;
import rainbow.vm.interpreter.visitor.Visitor;

import java.util.List;
import java.util.Map;

public class BoundSymbol extends ArcObject {
  public static final Symbol TYPE = Symbol.mkSym("bound-symbol");
  public final int nesting;
  protected final int index;
  public final Symbol name;

  public static BoundSymbol make(Symbol name, int nesting, int index) {
    return new BoundSymbol(name, nesting, index);
  }

  protected BoundSymbol(Symbol name, int nesting, int index) {
    this.nesting = nesting;
    this.index = index;
    this.name = name;
  }

  public void setSymbolValue(LexicalClosure lc, ArcObject value) {
    lc.nth(nesting).set(index, value);
  }

  public ArcObject interpret(LexicalClosure lc) {
    int n = nesting;
    while (n > 0) {
      lc = lc.parent;
      n--;
    }
    return lc.at(index);
  }

  public void addInstructions(List i) {
    i.add(new LexSym(this));
  }

  public ArcObject type() {
    return TYPE;
  }

  public String toString() {
    return name.toString();
  }

  public boolean isSameBoundSymbol(BoundSymbol other) {
    return nesting == other.nesting && name == other.name && index == other.index;
  }

  public BoundSymbol nest(int threshold) {
    if (nesting >= threshold) {
      return BoundSymbol.make(name, nesting + 1, index);
    } else {
      return this;
    }
  }

  public BoundSymbol unnest() {
    return BoundSymbol.make(name, this.nesting - 1, index);
  }

  public ArcObject inline(BoundSymbol p, ArcObject arg, boolean unnest, int nesting, int paramIndex) {
    if (this.isSameBoundSymbol(p)) {
      return arg;
    } else if (unnest) {
      if (this.nesting == 0) {
        throw new ArcError("can't unnest " + this + ", looking for " + p + " to inline with " + arg);
      }
      return unnest();
    } else if (nesting == this.nesting && paramIndex < index) {
      return BoundSymbol.make(name, this.nesting, this.index - 1);
    } else {
      return this;
    }
  }

  public void visit(Visitor v) {
    v.accept(this);
  }

  public ArcObject replaceBoundSymbols(Map<Symbol, Integer> lexicalBindings) {
    Integer index = lexicalBindings.get(name);
    if (index == null) {
      return this.unnest();
    } else if (index == this.index) {
      return new StackSymbol(name, index);
    } else {
      throw new ArcError("error: parameter index mismatch: expected " + index + ", got " + this.index);
    }
  }
}
