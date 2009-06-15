package rainbow.vm.interpreter;

import rainbow.types.ArcObject;
import rainbow.types.Symbol;
import rainbow.ArcError;
import rainbow.LexicalClosure;
import rainbow.vm.Continuation;
import rainbow.vm.continuations.AssignmentContinuation;

public class LastAssignment extends SingleAssignment {

  public void take(ArcObject o) {
    if (name == null) {
      if ((o instanceof Symbol) || (o instanceof BoundSymbol)) {
        name = o;
      } else {
        throw new ArcError("assign: can't assign to " + o);
      }
    } else if (expression == null) {
      expression = o;
    } else {
      throw new ArcError("assign: error: unexpected " + o);
    }
  }

  public void assign(LexicalClosure lc, Continuation caller, AssignmentContinuation assigning, ArcObject value) {
    name.setSymbolValue(lc, value);
    caller.receive(value);
  }
}
