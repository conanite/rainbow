package rainbow.vm.instructions.invoke;

import rainbow.types.ArcObject;
import rainbow.types.Pair;
import rainbow.types.Symbol;
import rainbow.vm.VM;
import rainbow.vm.Instruction;
import rainbow.vm.interpreter.BoundSymbol;

import java.util.List;

public class Invoke_1 {
  public static void addInstructions(List i, ArcObject fn, ArcObject arg) {
    arg.addInstructions(i);
    if (fn instanceof BoundSymbol) {
      i.add(new Lex(((BoundSymbol) fn)));
    } else if (fn instanceof Symbol) {
      i.add(new Free(((Symbol) fn)));
    } else {
      fn.addInstructions(i);
      i.add(new Other());
    }
  }

  private static class Lex extends Instruction implements Invoke {
    protected BoundSymbol fn;

    public Lex(BoundSymbol fn) {
      this.fn = fn;
    }

    public void operate(VM vm) {
      fn.interpret(vm.lc()).invoke(vm, new Pair(vm.popA(), NIL));
    }

    public String toString() {
      return "(invoke " + fn + " <1>)";
    }

    public ArcObject getInvokee(VM vm) {
      return fn.interpret(vm.lc());
    }
  }

  private static class Free extends Instruction implements Invoke {
    protected Symbol fn;

    public Free(Symbol fn) {
      this.fn = fn;
    }

    public void operate(VM vm) {
      fn.value().invoke(vm, new Pair(vm.popA(), NIL));
    }

    public String toString() {
      return "(invoke " + fn + " <1>)";
    }

    public ArcObject getInvokee(VM vm) {
      return fn.value();
    }
  }

  public static class Other extends Instruction implements Invoke {
    public void operate(VM vm) {
      vm.popA().invoke(vm, new Pair(vm.popA(), NIL));
    }

    public String toString() {
      return "(invoke <1>)";
    }

    public ArcObject getInvokee(VM vm) {
      return vm.peekA();
    }
  }
}
