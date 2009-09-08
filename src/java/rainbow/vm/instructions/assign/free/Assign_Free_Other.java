package rainbow.vm.instructions.assign.free;

import rainbow.LexicalClosure;
import rainbow.types.ArcObject;
import rainbow.types.Symbol;
import rainbow.vm.VM;

import java.util.List;

public class Assign_Free_Other extends Assign_Free {
  public Assign_Free_Other(Symbol name) {
    super(name);
  }

  public void operate(VM vm) {
    name.setValue(vm.peekA());
  }

  public String toString() {
    return "(assign-free-other " + name + ")";
  }

  public String toString(LexicalClosure lc) {
    return "(assign-free-other " + name + " -> " + symValue(name) + ")";
  }

  public static void addInstructions(List i, Symbol name, ArcObject expr, boolean last) {
    expr.addInstructions(i);
    if (last) {
      i.add(new Assign_Free_Other(name));
    } else {
      i.add(new Intermediate(name));
    }
  }

  public static class Intermediate extends Assign_Free_Other {
    public Intermediate(Symbol name) {
      super(name);
    }

    public void operate(VM vm) {
      name.setValue(vm.popA());
    }
  }
}
