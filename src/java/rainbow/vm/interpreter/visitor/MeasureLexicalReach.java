package rainbow.vm.interpreter.visitor;

import rainbow.vm.interpreter.BoundSymbol;
import rainbow.functions.interpreted.InterpretedFunction;
import rainbow.types.ArcObject;

import java.util.List;
import java.util.ArrayList;

public class MeasureLexicalReach extends Visitor {
  final List stack = new ArrayList();
  final List referrers = new ArrayList();
  private int nesting = 0;
  private int reach = -1;

  public int reach() {
    return reach;
  }

  public void accept(InterpretedFunction f) {
    if (f.nests()) {
      nesting++;
    }
  }

  public void acceptObject(ArcObject o) {
  }

  public void endObject(ArcObject o) {
  }

  public void end(InterpretedFunction f) {
    if (f.nests()) {
      nesting--;
    }
  }

  public void accept(BoundSymbol b) {
    int it = b.nesting - nesting;
    if (it > reach) {
      reach = it;
    }
  }
}
