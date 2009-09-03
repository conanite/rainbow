package rainbow.vm.instructions.assign.bound;

import rainbow.LexicalClosure;
import rainbow.types.ArcObject;
import rainbow.vm.VM;
import rainbow.vm.interpreter.BoundSymbol;

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
      i.add(new Intermediate(name, value));
    }
  }

  public String toString() {
    return "(assign-lex " + name + " " + value + ")";
  }

  public String toString(LexicalClosure lc) {
    return "(assign-lex " + name + "-->" + name.interpret(lc) + value + "-->" + value.interpret(lc) + ")";
  }

  public static class Intermediate extends Assign_Lex_Lex {
    public Intermediate(BoundSymbol name, BoundSymbol value) {
      super(name, value);
    }

    public void operate(VM vm) {
      name.setSymbolValue(vm.lc(), value.interpret(vm.lc()));
    }
  }
}
