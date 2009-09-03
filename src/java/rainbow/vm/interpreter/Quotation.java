package rainbow.vm.interpreter;

import rainbow.LexicalClosure;
import rainbow.types.ArcObject;
import rainbow.types.Symbol;
import rainbow.vm.instructions.Literal;

import java.util.List;

public class Quotation extends ArcObject {
  private ArcObject quoted;

  public Quotation(ArcObject quoted) {
    this.quoted = quoted;
  }

  public void addInstructions(List i) {
    i.add(new Literal(quoted));
  }

  public ArcObject quoted() {
    return quoted;
  }

  public ArcObject type() {
    return Symbol.mkSym("quotation");
  }

  public ArcObject interpret(LexicalClosure lc) {
    return quoted;
  }

  public String toString() {
    return "'" + quoted;
  }
}
