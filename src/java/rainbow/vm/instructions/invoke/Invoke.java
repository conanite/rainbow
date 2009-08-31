package rainbow.vm.instructions.invoke;

import rainbow.types.ArcObject;
import rainbow.vm.VM;

public interface Invoke {
  ArcObject getInvokee(VM vm);
}
