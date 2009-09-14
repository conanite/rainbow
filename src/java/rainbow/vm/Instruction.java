package rainbow.vm;

import rainbow.LexicalClosure;
import rainbow.vm.interpreter.visitor.Visitor;
import rainbow.types.ArcObject;
import rainbow.types.Symbol;

public abstract class Instruction extends ArcObject {
  private ArcObject owner;

  public ArcObject type() {
    return Symbol.mkSym("instruction");
  }

  public String toString(LexicalClosure lc) {
    return toString();
  }

  protected static String symValue(Symbol s) {
    if (s.bound()) {
      return String.valueOf(s.value());
    } else {
      return "#unbound#";
    }
  }

  public abstract void operate(VM vm);

  public void belongsTo(ArcObject fn) {
    this.owner = fn;
  }

  public void visit(Visitor v) {
    v.accept(this);
  }

  public ArcObject owner() {
    return owner;
  }
}
