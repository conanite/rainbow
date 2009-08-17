package rainbow.vm.instructions.assign.bound;

import rainbow.vm.interpreter.BoundSymbol;
import rainbow.vm.VM;
import rainbow.types.ArcObject;

import java.util.List;

public class Assign_Lex_Lex extends Assign_Lex {
  protected BoundSymbol value;

  public Assign_Lex_Lex(BoundSymbol name, BoundSymbol value) {
    super(name);
    this.value = value;
  }

  public void operate(VM vm) {
    ArcObject v = value.interpret(vm.lc());
    name.setSymbolValue(vm.lc(), v);
    vm.pushA(v);
  }

  public static void addInstructions(List i, BoundSymbol name, BoundSymbol value, boolean last) {
    if (last) {
      i.add(new Assign_Lex_Lex(name, value));
    } else {
      i.add(new Assign_Lex_Lex_Intermediate(name, value));
    }
  }
}
