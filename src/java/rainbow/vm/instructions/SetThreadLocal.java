package rainbow.vm.instructions;

import rainbow.vm.Instruction;
import rainbow.vm.VM;

public class SetThreadLocal extends Instruction {
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
