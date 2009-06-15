package rainbow.vm.continuations;

import rainbow.LexicalClosure;
import rainbow.types.ArcObject;
import rainbow.vm.ArcThread;
import rainbow.vm.Continuation;
import rainbow.vm.interpreter.SingleAssignment;

public class AssignmentContinuation extends ContinuationSupport {
  private SingleAssignment assignment;

  public AssignmentContinuation(ArcThread thread, LexicalClosure lc, Continuation caller) {
    super(thread, lc, caller);
  }

  public void continueWith(SingleAssignment assignment) {
    this.assignment = assignment;
    start();
  }

  public void start() {
    assignment.evaluate(thread, lc, this);
  }

  protected void onReceive(ArcObject returned) {
    assignment.assign(lc, caller, this, returned);
  }
}
