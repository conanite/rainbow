package rainbow.vm.interpreter;

import rainbow.ArcError;
import rainbow.LexicalClosure;
import rainbow.vm.ArcThread;
import rainbow.vm.Continuation;
import rainbow.vm.continuations.AssignmentContinuation;
import rainbow.types.ArcObject;
import rainbow.types.Symbol;

public class SingleAssignment {
  protected ArcObject name;
  protected ArcObject expression;
  private SingleAssignment next;

  public SingleAssignment() {
  }

  public SingleAssignment(SingleAssignment next) {
    this.next = next;
  }

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
      next.take(o);
    }
  }

  public void assign(LexicalClosure lc, Continuation caller, AssignmentContinuation assigning, ArcObject value) {
    name.setSymbolValue(lc, value);
    assigning.continueWith(next);
  }

  public void evaluate(ArcThread thread, LexicalClosure lc, AssignmentContinuation assigning) {
    expression.interpret(thread, lc, assigning);
  }
}
