package rainbow.vm.instructions.assign.free;

import rainbow.types.ArcObject;
import rainbow.types.Symbol;
import rainbow.vm.VM;

import java.util.List;

public class Assign_Free_Free extends Assign_Free {
  protected final Symbol value;

  public Assign_Free_Free(Symbol name, Symbol symbol) {
    super(name);
    this.value = symbol;
  }

  public void operate(VM vm) {
    ArcObject v = value.value();
    name.setValue(v);
    vm.pushA(v);
  }

  public static void addInstructions(List i, Symbol name, Symbol value, boolean last) {
    if (last) {
      i.add(new Assign_Free_Free(name, value));
    } else {
      i.add(new Intermediate(name, value));
    }
  }

  public String toString() {
    return "(assign-free-free " + name + " " + value + ")";
  }

  public static class Intermediate extends Assign_Free_Free {
    public Intermediate(Symbol name, Symbol value) {
      super(name, value);
    }

    public void operate(VM vm) {
      name.setValue(value.value());
    }
  }
}
