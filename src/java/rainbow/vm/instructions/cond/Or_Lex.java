package rainbow.vm.instructions.cond;

import rainbow.vm.Instruction;
import rainbow.vm.VM;
import rainbow.vm.interpreter.BoundSymbol;
import rainbow.LexicalClosure;
import rainbow.Nil;
import rainbow.types.ArcObject;

public class Or_Lex extends Instruction {
  private BoundSymbol ifExpr;
  private BoundSymbol elseExpr;

  public Or_Lex(BoundSymbol ifExpr, BoundSymbol elseExpr) {
    this.ifExpr = ifExpr;
    this.elseExpr = elseExpr;
  }

  public void operate(VM vm) {
    LexicalClosure lc = vm.lc();
    ArcObject cond = ifExpr.interpret(lc);
    if (cond instanceof Nil) {
      vm.pushA(elseExpr.interpret(lc));
    } else {
      vm.pushA(cond);
    }
  }
}
