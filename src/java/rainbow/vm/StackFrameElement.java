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
    String inv = "";
    if (invocation != null) {
      inv += invocation;
    }
    return continuation.getClass().getSimpleName() + " : " + inv;
  }
}
