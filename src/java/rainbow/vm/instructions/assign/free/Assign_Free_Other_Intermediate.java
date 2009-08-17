package rainbow.vm.instructions.assign.free;

import rainbow.types.Symbol;
import rainbow.vm.VM;

public class Assign_Free_Other_Intermediate extends Assign_Free_Other {
  public Assign_Free_Other_Intermediate(Symbol name) {
    super(name);
  }

  public void operate(VM vm) {
    name.setValue(vm.popA());
  }
}
