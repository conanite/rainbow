package rainbow.vm.instructions;

import rainbow.types.ArcObject;
import rainbow.vm.VM;
import rainbow.vm.Instruction;

public class Literal extends Instruction {
  ArcObject arg;

  public Literal(ArcObject arg) {
    this.arg = arg;
  }

  public void operate(VM vm) {
    vm.pushA(arg);
  }

  public String toString() {
    return "(literal " + arg + ")";
  }
}
