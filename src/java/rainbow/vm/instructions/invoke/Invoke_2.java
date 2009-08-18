package rainbow.vm.instructions.invoke;

import rainbow.types.ArcObject;
import rainbow.types.Pair;
import rainbow.types.Symbol;
import rainbow.vm.Instruction;
import rainbow.vm.VM;
import rainbow.vm.interpreter.BoundSymbol;
import rainbow.LexicalClosure;

import java.util.List;

public class Invoke_2 {

  public static void addInstructions(List i, ArcObject fn, ArcObject arg1, ArcObject arg2) {
    arg1.addInstructions(i);
    arg2.addInstructions(i);
    if (fn instanceof BoundSymbol) {
      i.add(new Lex(((BoundSymbol) fn)));
    } else if (fn instanceof Symbol) {
      i.add(new Free(((Symbol) fn)));
    } else {
      fn.addInstructions(i);
      i.add(new Other());
    }
  }

  private static class Lex extends Instruction {
    private BoundSymbol fn;

    public Lex(BoundSymbol fn) {
      this.fn = fn;
    }

    public void operate(VM vm) {
      ArcObject arg2 = vm.popA();
      ArcObject arg1 = vm.popA();
      fn.interpret(vm.lc()).invoke(vm, new Pair(arg1, new Pair(arg2, NIL)));
    }

    public String toString() {
      return "(invoke " + fn + " <2>)";
    }

    public String toString(LexicalClosure lc) {
      return "(invoke " + fn + " -> " + fn.interpret(lc) + " <2>)";
    }
  }

  private static class Free extends Instruction {
    private Symbol fn;

    public Free(Symbol fn) {
      this.fn = fn;
    }

    public void operate(VM vm) {
      ArcObject arg2 = vm.popA();
      ArcObject arg1 = vm.popA();
      fn.value().invoke(vm, new Pair(arg1, new Pair(arg2, NIL)));
    }

    public String toString() {
      return "(invoke " + fn + " <2>)";
    }

    public String toString(LexicalClosure lc) {
      return "(invoke " + fn + " -> " + fn.value() + " <2>)";
    }
  }

  private static class Other extends Instruction {
    public void operate(VM vm) {
      ArcObject f = vm.popA();
      ArcObject arg2 = vm.popA();
      ArcObject arg1 = vm.popA();
      try {
        f.invoke(vm, new Pair(arg1, new Pair(arg2, NIL)));
      } catch (Exception e) {
        e.printStackTrace();
      }
    }

    public String toString() {
      return "(invoke <2>)";
    }
  }
}
