package rainbow.vm.interpreter;

import rainbow.LexicalClosure;
import rainbow.types.ArcObject;
import rainbow.types.Symbol;
import rainbow.vm.Continuation;

public class Quotation extends ArcObject {
  private ArcObject quoted;

  public Quotation(ArcObject quoted) {
    this.quoted = quoted;
  }

  public ArcObject type() {
    return Symbol.make("quoatation");
  }

  public void interpret(LexicalClosure lc, Continuation caller) {
    caller.receive(quoted);
  }

  public String toString() {
    return "'" + quoted;
  }
}
