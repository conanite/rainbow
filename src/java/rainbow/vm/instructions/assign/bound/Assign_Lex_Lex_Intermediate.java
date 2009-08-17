package rainbow.vm.instructions.assign.bound;

import rainbow.vm.VM;
import rainbow.vm.interpreter.BoundSymbol;

public class Assign_Lex_Lex_Intermediate extends Assign_Lex_Lex {
  public Assign_Lex_Lex_Intermediate(BoundSymbol name, BoundSymbol value) {
    super(name, value);
  }

  public void operate(VM vm) {
    name.setSymbolValue(vm.lc(), value.interpret(vm.lc()));
  }
}
