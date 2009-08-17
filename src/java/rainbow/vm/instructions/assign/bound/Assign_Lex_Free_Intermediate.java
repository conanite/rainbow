package rainbow.vm.instructions.assign.bound;

import rainbow.types.Symbol;
import rainbow.vm.VM;
import rainbow.vm.interpreter.BoundSymbol;

public class Assign_Lex_Free_Intermediate extends Assign_Lex_Free {
  public Assign_Lex_Free_Intermediate(BoundSymbol name, Symbol value) {
    super(name, value);
  }

  public void operate(VM vm) {
    name.setSymbolValue(vm.lc(), value.value());
  }
}
