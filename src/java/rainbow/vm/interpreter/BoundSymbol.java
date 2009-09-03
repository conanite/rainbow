package rainbow.vm.interpreter;

import rainbow.ArcError;
import rainbow.LexicalClosure;
import rainbow.types.ArcObject;
import rainbow.types.Symbol;
import rainbow.vm.instructions.LexSym;
import rainbow.vm.interpreter.visitor.Visitor;

import java.util.List;

public class BoundSymbol extends ArcObject {
  public static final Symbol TYPE = Symbol.mkSym("bound-symbol");
  public final int nesting;
  private final int index;
  public final Symbol name;

  public BoundSymbol(Symbol name, int nesting, int index) {
    this.nesting = nesting;
    this.index = index;
    this.name = name;
  }

  public void setSymbolValue(LexicalClosure lc, ArcObject value) {
    lc.nth(nesting).set(index, value);
  }

  public ArcObject interpret(LexicalClosure lc) {
    try {
      return lc.nth(nesting).at(index);
    } catch (Exception e) {
      throw new ArcError("internal error evaluating " + this + " in context " + lc + "; " + e, e);
    }
  }

  public void addInstructions(List i) {
    i.add(new LexSym(this));
  }

  public ArcObject type() {
    return TYPE;
  }

  public String toString() {
    return name + "[" + nesting + ":" + index + "]";
//    return name.name();
  }

  public boolean isSameBoundSymbol(BoundSymbol other) {
    return nesting == other.nesting && name == other.name && index == other.index;
  }

  public int countReferences(int refs, BoundSymbol p) {
    if (isSameBoundSymbol(p)) {
      return refs + 1;
    } else {
      return refs;
    }
  }

  public int highestLexicalScopeReference() {
    return nesting;
  }

  public BoundSymbol nest(int threshold) {
    if (nesting >= threshold) {
      return new BoundSymbol(name, nesting + 1, index);
    } else {
      return this;
    }
  }

  public BoundSymbol unnest() {
    return new BoundSymbol(name, this.nesting - 1, index);
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
      return new BoundSymbol(name, this.nesting, this.index - 1);
    } else {
      return this;
    }
  }

  public void visit(Visitor v) {
    v.accept(this);
  }
}
