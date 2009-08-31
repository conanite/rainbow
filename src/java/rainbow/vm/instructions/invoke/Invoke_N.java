package rainbow.vm.instructions.invoke;

import rainbow.types.ArcObject;
import rainbow.types.Pair;
import rainbow.types.Symbol;
import rainbow.vm.Instruction;
import rainbow.vm.VM;
import rainbow.vm.interpreter.BoundSymbol;
import rainbow.Nil;

import java.util.List;

public class Invoke_N {
  public static void addInstructions(List i, ArcObject fn, Pair args) {
    int argCount = (int) args.len();
    while (!(args instanceof Nil)) {
      args.car().addInstructions(i);
      args = (Pair) args.cdr();
    }
    if (fn instanceof BoundSymbol) {
      i.add(new Lex((BoundSymbol) fn, argCount));
    } else if (fn instanceof Symbol) {
      i.add(new Free((Symbol) fn, argCount));
    } else {
      fn.addInstructions(i);
      i.add(new Other(argCount));
    }
  }

  private static class Lex extends Instruction implements Invoke {
    private BoundSymbol fn;
    private int argCount;

    public Lex(BoundSymbol fn, int argCount) {
      this.fn = fn;
      this.argCount = argCount;
    }

    public void operate(VM vm) {
      fn.interpret(vm.lc()).invoke(vm, vm.popArgs(argCount));
    }

    public String toString() {
      return "(invoke " + fn + " <3>)";
    }

    public ArcObject getInvokee(VM vm) {
      return fn.interpret(vm.lc());
    }
  }

  private static class Free extends Instruction implements Invoke {
    private Symbol fn;
    private int argCount;

    public Free(Symbol fn, int argCount) {
      this.fn = fn;
      this.argCount = argCount;
    }

    public void operate(VM vm) {
      fn.value().invoke(vm, vm.popArgs(argCount));
    }

    public String toString() {
      return "(invoke " + fn + " <3>)";
    }

    public ArcObject getInvokee(VM vm) {
      return fn;
    }
  }

  public static class Other extends Instruction implements Invoke {
    private int argCount;

    public Other(int argCount) {
      this.argCount = argCount;
    }

    public void operate(VM vm) {
      ArcObject f = vm.popA();
      f.invoke(vm, vm.popArgs(argCount));
    }

    public String toString() {
      return "(invoke <" + argCount + ">)";
    }

    public ArcObject getInvokee(VM vm) {
      return vm.peekA();
    }
  }
}
