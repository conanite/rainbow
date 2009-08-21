package rainbow.vm.instructions.cond;

import rainbow.vm.Instruction;
import rainbow.vm.VM;
import rainbow.vm.interpreter.BoundSymbol;
import rainbow.types.ArcObject;
import rainbow.LexicalClosure;
import rainbow.Nil;

public class Or_Literal extends Instruction {
  private BoundSymbol ifExpr;
  private ArcObject e;

  public Or_Literal(BoundSymbol ifExpr, ArcObject e) {
    this.ifExpr = ifExpr;
    this.e = e;
  }

  public void operate(VM vm) {
    LexicalClosure lc = vm.lc();
    ArcObject cond = ifExpr.interpret(lc);
    if (cond instanceof Nil) {
      vm.pushA(e);
    } else {
      vm.pushA(cond);
    }
  }
}
