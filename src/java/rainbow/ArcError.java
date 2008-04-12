package rainbow;

import rainbow.vm.StackFrameElement;
import rainbow.vm.Continuation;
import rainbow.types.ArcObject;

import java.util.List;
import java.util.LinkedList;
import java.util.Iterator;

public class ArcError extends RuntimeException {
  private List arcStack = new LinkedList();

  public ArcError(String message) {
    super(message);
  }

  public ArcError(String message, Throwable e) {
    super(message, e);
  }

  public ArcError(Exception e) {
    super(e);
  }

  public String getMessage() {
    return super.getMessage();
  }

  public String getStacktrace() {
    StringBuilder m = new StringBuilder();
    for (Iterator i = arcStack.iterator(); i.hasNext();) {
      m.append("\n").append(i.next());
    }
    return m.toString();
  }

  public ArcError addStackFrame(StackFrameElement sfe) {
    arcStack.add(sfe);
    return this;
  }

  public void addStackFrame(Continuation continuation, ArcObject o) {
    addStackFrame(new StackFrameElement(continuation, o));
  }
}
