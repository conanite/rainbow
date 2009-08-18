package rainbow.vm;

import rainbow.types.ArcObject;
import rainbow.types.Symbol;
import rainbow.LexicalClosure;

public abstract class Instruction extends ArcObject {
  public abstract void operate(VM vm);

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
}
