package rainbow.vm.instructions;

import rainbow.types.ArcObject;
import rainbow.vm.Instruction;
import rainbow.vm.VM;

public class PopArg extends Instruction {
  String name;
  private ArcObject previous;

  public PopArg(String name) {
    this.name = name;
  }

  public PopArg(String name, ArcObject previous) {
    this.name = name;
    this.previous = previous;
  }

  public void operate(VM vm) {
    try {
//      System.out.println("Pop-Arg:");
//      vm.show();
      vm.popA();
    } catch (Throwable t) {
      throw new Error("arg stack is empty! " + name + " after " + previous, t);
    }
  }

  public String toString() {
    return "(pop-arg)";
  }
}
