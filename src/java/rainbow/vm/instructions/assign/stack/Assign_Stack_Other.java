package rainbow.vm.instructions.assign.stack;

import rainbow.types.ArcObject;
import rainbow.vm.VM;
import rainbow.vm.interpreter.StackSymbol;

import java.util.List;

public class Assign_Stack_Other extends Assign_Stack {
  public Assign_Stack_Other(StackSymbol name) {
    super(name);
  }

  public void operate(VM vm) {
    name.set(vm, vm.peekA());
  }

  public String toString() {
    return "(assign-stack " + name + ")";
  }

  public static void addInstructions(List i, StackSymbol name, ArcObject expr, boolean last) {
    expr.addInstructions(i);
    if (last) {
      i.add(new Assign_Stack_Other(name));
    } else {
      i.add(new Intermediate(name));
    }
  }

  public static class Intermediate extends Assign_Stack_Other {
    public Intermediate(StackSymbol name) {
      super(name);
    }

    public void operate(VM vm) {
      name.set(vm, vm.popA());
    }
  }
}
