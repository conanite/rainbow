package rainbow.vm.instructions.assign.free;

import rainbow.types.Symbol;
import rainbow.vm.VM;

public class Assign_Free_Free_Intermediate extends Assign_Free_Free {
  public Assign_Free_Free_Intermediate(Symbol name, Symbol value) {
    super(name, value);
  }

  public void operate(VM vm) {
    name.setValue(value.value());
  }
}
