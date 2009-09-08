package rainbow.vm.interpreter;

import rainbow.types.ArcObject;
import rainbow.types.Symbol;
import rainbow.vm.VM;
import rainbow.vm.instructions.StackSym;

import java.util.List;

public class StackSymbol extends ArcObject {
  public Symbol name;
  private int index;

  public StackSymbol(Symbol name, int index) {
    this.name = name;
    this.index = index;
  }

  public ArcObject get(VM vm) {
    return vm.param(index);
  }

  public void set(VM vm, ArcObject newValue) {
    vm.params[vm.ip][index] = newValue;
  }

  public void addInstructions(List i) {
    i.add(new StackSym(name, index));
  }

  public ArcObject type() {
    return Symbol.mkSym("stack-symbol");
  }

  public String toString() {
    return name + "[" + index + "]";
  }

  public boolean isSameStackSymbol(StackSymbol b2) {
    return (this.name == b2.name) && (this.index == b2.index);
  }

  public ArcObject inline(StackSymbol p, ArcObject arg, int paramIndex) {
    if (isSameStackSymbol(p)) {
      return arg;
    } else if (paramIndex < index) {
      return new StackSymbol(name, index - 1);
    } else {
      return this;
    }
  }
}
