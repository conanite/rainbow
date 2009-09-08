package rainbow.vm.instructions.assign.free;

import rainbow.types.ArcObject;
import rainbow.types.Symbol;
import rainbow.vm.VM;

import java.util.List;

public class Assign_Free_Literal extends Assign_Free{
  protected ArcObject expr;

  public Assign_Free_Literal(Symbol name, ArcObject expr) {
    super(name);
    this.expr = expr;
  }

  public void operate(VM vm) {
    name.setValue(expr);
    vm.pushA(expr);
  }

  public static void addInstructions(List i, Symbol name, ArcObject expr, boolean last) {
    if (last) {
      i.add(new Assign_Free_Literal(name, expr));
    } else {
      i.add(new Intermediate(name, expr));
    }
  }

  public String toString() {
    return "(assign-free-literal " + name + " " + expr + ")";
  }

  public static class Intermediate extends Assign_Free_Literal {
    public Intermediate(Symbol name, ArcObject expr) {
      super(name, expr);
    }

    public void operate(VM vm) {
      name.setValue(expr);
    }
  }
}

