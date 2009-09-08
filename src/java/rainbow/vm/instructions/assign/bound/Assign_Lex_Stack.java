package rainbow.vm.instructions.assign.bound;

import rainbow.LexicalClosure;
import rainbow.types.ArcObject;
import rainbow.vm.VM;
import rainbow.vm.interpreter.BoundSymbol;
import rainbow.vm.interpreter.StackSymbol;

import java.util.List;

public class Assign_Lex_Stack extends Assign_Lex {
  protected StackSymbol value;

  public Assign_Lex_Stack(BoundSymbol name, StackSymbol value) {
    super(name);
    this.value = value;
  }

  public void operate(VM vm) {
    ArcObject v = value.get(vm);
    name.setSymbolValue(vm.lc(), v);
    vm.pushA(v);
  }

  public static void addInstructions(List i, BoundSymbol name, StackSymbol value, boolean last) {
    if (last) {
      i.add(new Assign_Lex_Stack(name, value));
    } else {
      i.add(new Intermediate(name, value));
    }
  }

  public String toString() {
    return "(assign-lex " + name + " " + value + ")";
  }

  public String toString(LexicalClosure lc) {
    return "(assign-lex " + name + "-->" + name.interpret(lc) + " <-- " + value + ")";
  }

  public static class Intermediate extends Assign_Lex_Stack {
    public Intermediate(BoundSymbol name, StackSymbol value) {
      super(name, value);
    }

    public void operate(VM vm) {
      name.setSymbolValue(vm.lc(), value.get(vm));
    }
  }
}
