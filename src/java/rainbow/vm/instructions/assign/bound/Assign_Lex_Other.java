package rainbow.vm.instructions.assign.bound;

import rainbow.LexicalClosure;
import rainbow.types.ArcObject;
import rainbow.vm.VM;
import rainbow.vm.interpreter.BoundSymbol;

import java.util.List;

public class Assign_Lex_Other extends Assign_Lex {
  public Assign_Lex_Other(BoundSymbol name) {
    super(name);
  }

  public void operate(VM vm) {
    name.setSymbolValue(vm.lc(), vm.peekA());
  }

  public String toString() {
    return "(assign-lex " + name + ")";
  }

  public String toString(LexicalClosure lc) {
    return "(assign-lex " + name + " -> " + name.interpret(lc) + ")";
  }

  public static void addInstructions(List i, BoundSymbol name, ArcObject expr, boolean last) {
    expr.addInstructions(i);
    if (last) {
      i.add(new Assign_Lex_Other(name));
    } else {
      i.add(new Assign_Lex_Other_Intermediate(name));
    }
  }
}
