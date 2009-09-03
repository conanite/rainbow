package rainbow.vm.instructions;

import rainbow.vm.VM;
import rainbow.vm.Instruction;
import rainbow.vm.interpreter.BoundSymbol;
import rainbow.LexicalClosure;

public class LexSym extends Instruction {
  BoundSymbol sym;

  public LexSym(BoundSymbol sym) {
    this.sym = sym;
  }

  public void operate(VM vm) {
    vm.pushA(sym.interpret(vm.lc()));
  }

  public String toString() {
    return "(lex-sym " + sym + ")";
  }

  public String toString(LexicalClosure lc) {
    return "(lex-sym " + sym + " -> " + sym.interpret(lc) + ")";
  }
}
