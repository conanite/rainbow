package rainbow.vm.instructions.invoke;

import rainbow.types.ArcObject;
import rainbow.types.Symbol;
import rainbow.vm.Instruction;
import rainbow.vm.VM;
import rainbow.vm.interpreter.BoundSymbol;

import java.util.List;

public class Invoke_0 {
  public static void addInstructions(List i, ArcObject fn) {
    if (fn instanceof BoundSymbol) {
      i.add(new Lex(((BoundSymbol) fn)));
    } else if (fn instanceof Symbol) {
      i.add(new Free(((Symbol) fn)));
    } else {
      fn.addInstructions(i);
      i.add(new Other());
    }
  }

  static class Lex extends Instruction {
    private BoundSymbol fn;

    public Lex(BoundSymbol fn) {
      this.fn = fn;
    }

    public void operate(VM vm) {
      fn.interpret(vm.lc()).invoke(vm, NIL);
    }

    public String toString() {
      return "(invoke " + fn + ")";
    }
  }

  static class Free extends Instruction {
    private Symbol fn;

    public Free(Symbol fn) {
      this.fn = fn;
    }

    public void operate(VM vm) {
      fn.value().invoke(vm, NIL);
    }

    public String toString() {
      return "(invoke " + fn + ")";
    }
  }

  static class Other extends Instruction {
    public void operate(VM vm) {
      vm.popA().invoke(vm, NIL);
    }

    public String toString() {
      return "(invoke <0>)";
    }
  }
}
