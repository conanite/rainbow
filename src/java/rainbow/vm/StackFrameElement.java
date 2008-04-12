package rainbow.vm;

import rainbow.types.ArcObject;

public class StackFrameElement {
  private Continuation continuation;
  private ArcObject invocation;

  public StackFrameElement(Continuation continuation, ArcObject invocation) {
    this.continuation = continuation;
    this.invocation = invocation;
  }

  public String toString() {
    return continuation.getClass().getSimpleName() + " : " + invocation + " at " + invocation.source();
  }
}
