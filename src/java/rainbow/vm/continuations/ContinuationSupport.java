package rainbow.vm.continuations;

import rainbow.ArcError;
import rainbow.LexicalClosure;
import rainbow.types.ArcObject;
import rainbow.vm.ArcThread;
import rainbow.vm.Continuation;
import rainbow.vm.StackFrameElement;

public abstract class ContinuationSupport implements Continuation, Cloneable {
  protected ArcThread thread;
  protected LexicalClosure lc;
  protected Continuation caller;
  protected boolean stopped;

  public ContinuationSupport(ArcThread thread, LexicalClosure lc, Continuation caller) {
    this.thread = thread;
    this.lc = lc;
    this.caller = caller;
  }

  public final void receive(ArcObject returned) {
    if (!stopped) {
      try {
        onReceive(returned);
      } catch (ArcError ae) {
        error(ae);
      } catch (Exception e) {
        error(new ArcError(e));
      }
    }
  }

  protected abstract void onReceive(ArcObject returned);

  public void error(ArcError error) {
    ArcObject invocation = getCurrentTarget();
    if (invocation == null) {
      throw new NullPointerException("invocation must not be null in " + getClass());
    }
    error.addStackFrame(new StackFrameElement(this, invocation));
    caller.error(error);
  }

  protected ArcObject getCurrentTarget() {
    return ArcObject.NIL;
  }

  public Continuation cloneFor(ArcThread thread) {
    try {
      ContinuationSupport e = (ContinuationSupport) super.clone();
      e.thread = thread;
      if (caller != null) {
        e.caller = this.caller.cloneFor(thread);
      }
      return e;
    } catch (CloneNotSupportedException e1) {
      throw new ArcError(e1);
    }
  }

  public void stop() {
    this.stopped = true;
    caller.stop();
  }
}
