package rainbow.vm.instructions;

import rainbow.types.ArcException;
import rainbow.types.ArcObject;
import rainbow.vm.Instruction;
import rainbow.vm.VM;

public class Literal extends Instruction {
  ArcObject arg;

  public Literal(ArcObject arg) {
    this.arg = arg;
  }

  public void operate(VM vm) {
    if (arg instanceof ArcException) {
      System.out.println("Literal:pushing ArcException " + arg);
    }
    vm.pushA(arg);
  }

  public String toString() {
    return "(literal " + arg + ")";
  }
}
