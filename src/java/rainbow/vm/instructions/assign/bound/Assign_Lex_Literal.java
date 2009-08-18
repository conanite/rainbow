package rainbow.vm.instructions.assign.bound;

import rainbow.types.ArcObject;
import rainbow.vm.interpreter.BoundSymbol;
import rainbow.vm.VM;

import java.util.List;

public class Assign_Lex_Literal extends Assign_Lex {
  protected ArcObject expr;

  public Assign_Lex_Literal(BoundSymbol name, ArcObject expr) {
    super(name);
    this.expr = expr;
  }

  public void operate(VM vm) {
    name.setSymbolValue(vm.lc(), expr);
    vm.pushA(expr);
  }

  public static void addInstructions(List i, BoundSymbol name, ArcObject expr, boolean last) {
    if (last) {
      i.add(new Assign_Lex_Literal(name, expr));
    } else {
      i.add(new Assign_Lex_Literal_Intermediate(name, expr));
    }
  }

  public String toString() {
    return "(assign-lex " + name + " " + expr + ")";
  }
}
