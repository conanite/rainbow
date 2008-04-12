package rainbow.vm.continuations;

import rainbow.vm.ArcThread;
import rainbow.vm.Continuation;
import rainbow.vm.StackFrameElement;
import rainbow.Bindings;
import rainbow.ArcError;
import rainbow.types.ArcObject;

public abstract class ContinuationSupport implements Continuation, Cloneable {
  protected ArcThread thread;
  protected Bindings namespace;
  protected Continuation whatToDo;
  protected boolean stopped;

  public ContinuationSupport(ArcThread thread, Bindings namespace, Continuation whatToDo) {
    this.thread = thread;
    this.namespace = namespace;
    this.whatToDo = whatToDo;
  }

  public final void eat(ArcObject returned) {
    if (stopped) {
      return;
    }
    digest(returned);
  }

  protected abstract void digest(ArcObject returned);

  public void error(ArcError error) {
    ArcObject invocation = getCurrentTarget();
    if (invocation == null) {
      throw new NullPointerException("invocation must not be null in " + getClass());
    }
    error.addStackFrame(new StackFrameElement(this, invocation));
    whatToDo.error(error);
  }

  protected ArcObject getCurrentTarget() {
    return ArcObject.NIL;
  }

  public Continuation cloneFor(ArcThread thread) {
    try {
      ContinuationSupport e = (ContinuationSupport) super.clone();
      e.thread = thread;
      if (whatToDo != null) {
        e.whatToDo = this.whatToDo.cloneFor(thread);
      }
      return e;
    } catch (CloneNotSupportedException e1) {
      throw new ArcError(e1);
    }
  }

  public void stop() {
    this.stopped = true;
    whatToDo.stop();
  }
}

