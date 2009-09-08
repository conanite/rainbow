package rainbow.vm.instructions.assign.free;

import rainbow.types.ArcObject;
import rainbow.types.Symbol;
import rainbow.vm.VM;
import rainbow.vm.interpreter.StackSymbol;

import java.util.List;

public class Assign_Free_Stack extends Assign_Free {
  protected StackSymbol value;

  public Assign_Free_Stack(Symbol name, StackSymbol value) {
    super(name);
    this.value = value;
  }

  public void operate(VM vm) {
    ArcObject v = value.get(vm);
    name.setValue(v);
    vm.pushA(v);
  }

  public static void addInstructions(List i, Symbol name, StackSymbol value, boolean last) {
    if (last) {
      i.add(new Assign_Free_Stack(name, value));
    } else {
      i.add(new Intermediate(name, value));
    }
  }

  public String toString() {
    return "(assign-free-stack " + name + " " + value + ")";
  }

  public static class Intermediate extends Assign_Free_Stack {
    public Intermediate(Symbol name, StackSymbol value) {
      super(name, value);
    }

    public void operate(VM vm) {
      name.setValue(value.get(vm));
    }
  }
}
