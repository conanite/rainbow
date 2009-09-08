package rainbow.vm.instructions.assign.stack;

import rainbow.types.ArcObject;
import rainbow.vm.VM;
import rainbow.vm.interpreter.StackSymbol;

import java.util.List;

public class Assign_Stack_Stack extends Assign_Stack {
  protected StackSymbol value;

  public Assign_Stack_Stack(StackSymbol name, StackSymbol value) {
    super(name);
    this.value = value;
  }

  public void operate(VM vm) {
    ArcObject v = value.get(vm);
    name.set(vm, v);
    vm.pushA(v);
  }

  public static void addInstructions(List i, StackSymbol name, StackSymbol value, boolean last) {
    if (last) {
      i.add(new Assign_Stack_Stack(name, value));
    } else {
      i.add(new Intermediate(name, value));
    }
  }

  public String toString() {
    return "(assign-free " + name + " " + value + ")";
  }

  public static class Intermediate extends Assign_Stack_Stack {
    public Intermediate(StackSymbol name, StackSymbol value) {
      super(name, value);
    }

    public void operate(VM vm) {
      name.set(vm, value.get(vm));
    }
  }
}
