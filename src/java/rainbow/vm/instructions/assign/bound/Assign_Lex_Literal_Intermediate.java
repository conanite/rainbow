package rainbow.vm.instructions.assign.bound;

import rainbow.types.ArcObject;
import rainbow.vm.interpreter.BoundSymbol;
import rainbow.vm.VM;

public class Assign_Lex_Literal_Intermediate extends Assign_Lex_Literal {
  public Assign_Lex_Literal_Intermediate(BoundSymbol name, ArcObject expr) {
    super(name, expr);
  }

  public void operate(VM vm) {
    name.setSymbolValue(vm.lc(), expr);
  }
}
