package rainbow.vm.instructions.assign.stack;

import rainbow.types.ArcObject;
import rainbow.vm.VM;
import rainbow.vm.interpreter.BoundSymbol;
import rainbow.vm.interpreter.StackSymbol;

import java.util.List;

public class Assign_Stack_Lex extends Assign_Stack {
  protected BoundSymbol value;

  public Assign_Stack_Lex(StackSymbol name, BoundSymbol value) {
    super(name);
    this.value = value;
  }

  public void operate(VM vm) {
    ArcObject v = value.interpret(vm.lc());
    name.set(vm, v);
    vm.pushA(v);
  }

  public static void addInstructions(List i, StackSymbol name, BoundSymbol value, boolean last) {
    if (last) {
      i.add(new Assign_Stack_Lex(name, value));
    } else {
      i.add(new Intermediate(name, value));
    }
  }

  public String toString() {
    return "(assign-free " + name + " " + value + ")";
  }

  public static class Intermediate extends Assign_Stack_Lex {
    public Intermediate(StackSymbol name, BoundSymbol value) {
      super(name, value);
    }

    public void operate(VM vm) {
      name.set(vm, value.interpret(vm.lc()));
    }
  }
}
