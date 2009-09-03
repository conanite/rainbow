package rainbow.vm.instructions.assign.bound;

import rainbow.vm.interpreter.BoundSymbol;
import rainbow.vm.VM;
import rainbow.types.Symbol;
import rainbow.types.ArcObject;

import java.util.List;

public class Assign_Lex_Free extends Assign_Lex {
  protected final Symbol value;

  public Assign_Lex_Free(BoundSymbol name, Symbol symbol) {
    super(name);
    this.value = symbol;
  }

  public void operate(VM vm) {
    ArcObject v = value.value();
    name.setSymbolValue(vm.lc(), v);
    vm.pushA(v);
  }

  public static void addInstructions(List i, BoundSymbol name, Symbol symbol, boolean last) {
    if (last) {
      i.add(new Assign_Lex_Free(name, symbol));
    } else {
      i.add(new Intermediate(name, symbol));
    }
  }

  public String toString() {
    return "(assign-lex " + name + " " + value + ")";
  }

  public static class Intermediate extends Assign_Lex_Free {
    public Intermediate(BoundSymbol name, Symbol value) {
      super(name, value);
    }

    public void operate(VM vm) {
      name.setSymbolValue(vm.lc(), value.value());
    }
  }
}
