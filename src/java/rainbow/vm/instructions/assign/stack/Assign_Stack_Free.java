package rainbow.vm.instructions.assign.stack;

import rainbow.types.ArcObject;
import rainbow.types.Symbol;
import rainbow.vm.VM;
import rainbow.vm.interpreter.StackSymbol;

import java.util.List;

public class Assign_Stack_Free extends Assign_Stack {
  protected Symbol value;

  public Assign_Stack_Free(StackSymbol name, Symbol value) {
    super(name);
    this.value = value;
  }

  public void operate(VM vm) {
    ArcObject v = value.value();
    name.set(vm, v);
    vm.pushA(v);
  }

  public static void addInstructions(List i, StackSymbol name, Symbol value, boolean last) {
    if (last) {
      i.add(new Assign_Stack_Free(name, value));
    } else {
      i.add(new Intermediate(name, value));
    }
  }

  public String toString() {
    return "(assign-free " + name + " " + value + ")";
  }

  public static class Intermediate extends Assign_Stack_Free {
    public Intermediate(StackSymbol name, Symbol value) {
      super(name, value);
    }

    public void operate(VM vm) {
      name.set(vm, value.value());
    }
  }
}
