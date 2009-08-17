package rainbow.vm.instructions;

import rainbow.vm.Instruction;
import rainbow.vm.VM;
import rainbow.types.Symbol;
import rainbow.LexicalClosure;

public class FreeSym extends Instruction {
  Symbol sym;

  public FreeSym(Symbol sym) {
    this.sym = sym;
  }

  public void operate(VM vm) {
    vm.pushA(sym.value());
  }

  public String toString() {
    return "(free-sym " + sym + ")";
  }

  public String toString(LexicalClosure lc) {
    return "(free-sym " + sym + " -> " + symValue(sym) + ")";
  }
}
