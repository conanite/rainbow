package rainbow.vm.interpreter;

import rainbow.ArcError;
import rainbow.LexicalClosure;
import rainbow.types.ArcObject;
import rainbow.types.Symbol;
import rainbow.vm.ArcThread;
import rainbow.vm.Continuation;
import rainbow.vm.continuations.AssignmentContinuation;

public class Assignment extends ArcObject {
  private SingleAssignment assignment;

  public ArcObject type() {
    return Symbol.make("assignment");
  }

  public void add(ArcObject o) {
    assignment.take(o);
  }

  public void interpret(ArcThread thread, LexicalClosure lc, Continuation caller) {
    new AssignmentContinuation(thread, lc, caller).continueWith(assignment);
  }

  public void prepare(int size) {
    if (size % 2 != 0) {
      throw new ArcError("assign: requires even number of arguments");
    }

    size = (size / 2) - 1;
    if (size < 0) {
      throw new ArcError("assign: nothing to assign");
    }

    assignment = new LastAssignment();
    while (size > 0) {
      assignment = new SingleAssignment(assignment);
      size--;
    }
  }
}
