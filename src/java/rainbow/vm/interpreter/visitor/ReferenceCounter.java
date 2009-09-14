package rainbow.vm.interpreter.visitor;

import rainbow.functions.interpreted.InterpretedFunction;
import rainbow.types.ArcObject;
import rainbow.vm.interpreter.BoundSymbol;

import java.util.ArrayList;
import java.util.List;

public class ReferenceCounter extends Visitor {
  final List stack = new ArrayList();
  final List referrers = new ArrayList();
  BoundSymbol target;

  public ReferenceCounter(BoundSymbol target) {
    this.target = target;
  }

  public int count() {
    return referrers.size();
  }

  public void accept(InterpretedFunction f) {
    stack.add(0, f);
    if (f.nests()) {
      target = target.nest(0);
    }
  }

  public void acceptObject(ArcObject o) {
    stack.add(0, o);
  }

  public void endObject(ArcObject o) {
    stack.remove(0);
  }

  public void end(InterpretedFunction f) {
    stack.remove(0);
    if (f.nests()) {
      target = target.unnest();
    }
  }

  public void accept(BoundSymbol b) {
    if (b.isSameBoundSymbol(target) && stack.size() > 0) {
      referrers.add(stack.get(0));
    }
  }
}
