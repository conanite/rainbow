package rainbow.vm.interpreter.visitor;

import rainbow.functions.interpreted.InterpretedFunction;
import rainbow.types.ArcObject;
import rainbow.types.Pair;

public class FunctionOwnershipVisitor extends Visitor {
  private Pair owners;

  public FunctionOwnershipVisitor(InterpretedFunction top) {
    owners = new Pair(top, ArcObject.NIL);
  }

  public void accept(InterpretedFunction o) {
    o.belongsTo((InterpretedFunction)owners.car());
    owners = new Pair(o, owners);
  }

  public void end(InterpretedFunction o) {
    owners = (Pair) owners.cdr();
  }
}
