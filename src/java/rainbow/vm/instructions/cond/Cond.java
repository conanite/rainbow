package rainbow.vm.instructions.cond;

import rainbow.Nil;
import rainbow.types.ArcObject;
import rainbow.types.Pair;
import rainbow.vm.Instruction;
import rainbow.vm.VM;
import rainbow.vm.interpreter.visitor.Visitor;

import java.util.ArrayList;
import java.util.List;

public class Cond extends Instruction {
  private ArcObject thenExpr;
  private ArcObject elseExpr;
  private Pair thenInstructions;
  private Pair elseInstructions;

  public Cond(ArcObject thenExpr, ArcObject elseExpr, String sig) {
    this.thenExpr = thenExpr;
    this.thenInstructions = instructionsFor(thenExpr);
    this.elseExpr = elseExpr;
    this.elseInstructions = instructionsFor(elseExpr);
  }

  public static Pair instructionsFor(ArcObject expr) {
    List list = new ArrayList();
    expr.addInstructions(list);
    return Pair.buildFrom(list);
  }

  public void operate(VM vm) {
    vm.pushConditional((vm.popA() instanceof Nil) ? elseInstructions : thenInstructions);
  }

  public void visit(Visitor v) {
    super.visit(v);
    thenInstructions.visit(v);
    elseInstructions.visit(v);
  }

  public String toString() {
    return "(cond then:" + thenExpr + ", else:" + elseExpr + ")";
  }
}
