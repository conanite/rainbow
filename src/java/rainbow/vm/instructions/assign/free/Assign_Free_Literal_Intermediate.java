package rainbow.vm.instructions.assign.free;

import rainbow.types.ArcObject;
import rainbow.types.Symbol;
import rainbow.vm.VM;

public class Assign_Free_Literal_Intermediate extends Assign_Free_Literal {
  public Assign_Free_Literal_Intermediate(Symbol name, ArcObject expr) {
    super(name, expr);
  }

  public void operate(VM vm) {
    name.setValue(expr);
  }
}
