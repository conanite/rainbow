package rainbow.vm.instructions.assign.stack;

import rainbow.types.Symbol;
import rainbow.types.ArcObject;
import rainbow.vm.interpreter.StackSymbol;
import rainbow.vm.VM;

import java.util.List;

public class Assign_Stack_Literal extends Assign_Stack {
  protected ArcObject value;

  public Assign_Stack_Literal(StackSymbol name, ArcObject value) {
    super(name);
    this.value = value;
  }

  public void operate(VM vm) {
    name.set(vm, value);
    vm.pushA(value);
  }

  public static void addInstructions(List i, StackSymbol name, Symbol value, boolean last) {
    if (last) {
      i.add(new Assign_Stack_Literal(name, value));
    } else {
      i.add(new Intermediate(name, value));
    }
  }

  public String toString() {
    return "(assign-free " + name + " " + value + ")";
  }

  public static class Intermediate extends Assign_Stack_Literal {
    public Intermediate(StackSymbol name, ArcObject value) {
      super(name, value);
    }

    public void operate(VM vm) {
      name.set(vm, value);
    }
  }
}
