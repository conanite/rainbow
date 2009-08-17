package rainbow.vm.instructions.assign.free;

import rainbow.types.Symbol;
import rainbow.vm.VM;
import rainbow.vm.interpreter.BoundSymbol;

public class Assign_Free_Lex_Intermediate extends Assign_Free_Lex {
  public Assign_Free_Lex_Intermediate(Symbol name, BoundSymbol value) {
    super(name, value);
  }

  public void operate(VM vm) {
    name.setValue(value.interpret(vm.lc()));
  }
}
