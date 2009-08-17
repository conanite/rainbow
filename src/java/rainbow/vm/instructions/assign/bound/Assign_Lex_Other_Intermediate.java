package rainbow.vm.instructions.assign.bound;

import rainbow.vm.VM;
import rainbow.vm.interpreter.BoundSymbol;

public class Assign_Lex_Other_Intermediate extends Assign_Lex_Other {
  public Assign_Lex_Other_Intermediate(BoundSymbol name) {
    super(name);
  }

  public void operate(VM vm) {
    name.setSymbolValue(vm.lc(), vm.popA());
  }
}
