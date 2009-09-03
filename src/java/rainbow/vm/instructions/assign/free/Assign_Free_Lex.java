package rainbow.vm.instructions.assign.free;

import rainbow.types.ArcObject;
import rainbow.types.Symbol;
import rainbow.vm.VM;
import rainbow.vm.interpreter.BoundSymbol;

import java.util.List;

public class Assign_Free_Lex extends Assign_Free {
  protected BoundSymbol value;

  public Assign_Free_Lex(Symbol name, BoundSymbol value) {
    super(name);
    this.value = value;
  }

  public void operate(VM vm) {
    ArcObject v = value.interpret(vm.lc());
    name.setValue(v);
    vm.pushA(v);
  }

  public static void addInstructions(List i, Symbol name, BoundSymbol value, boolean last) {
    if (last) {
      i.add(new Assign_Free_Lex(name, value));
    } else {
      i.add(new Intermediate(name, value));
    }
  }

  public String toString() {
    return "(assign-free " + name + " " + value + ")";
  }

  public static class Intermediate extends Assign_Free_Lex {
    public Intermediate(Symbol name, BoundSymbol value) {
      super(name, value);
    }

    public void operate(VM vm) {
      name.setValue(value.interpret(vm.lc()));
    }
  }
}
