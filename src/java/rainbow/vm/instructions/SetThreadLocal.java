package rainbow.vm.instructions;

import rainbow.vm.VM;
import rainbow.vm.Instruction;

public class SetThreadLocal extends Instruction implements Finally {
  private final ThreadLocal tl;
  private final Object value;

  public SetThreadLocal(ThreadLocal tl, Object value) {
    this.tl = tl;
    this.value = value;
  }

  public void operate(VM vm) {
    tl.set(value);
  }
}
