package rainbow.vm.interpreter;

import rainbow.LexicalClosure;
import rainbow.vm.ArcThread;
import rainbow.vm.Continuation;
import rainbow.types.ArcObject;
import rainbow.types.Symbol;

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

  public void setSymbolValue(LexicalClosure lc, ArcObject value) {
    lc.nth(nesting).set(index, value);
  }

  public void interpret(ArcThread thread, LexicalClosure lc, Continuation caller) {
    caller.receive(lc.nth(nesting).at(index));
  }

  public ArcObject type() {
    return TYPE;
  }

  public String toString() {
    return name.name();
  }
}
