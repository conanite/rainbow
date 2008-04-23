package rainbow.vm.continuations;

import rainbow.vm.ArcThread;
import rainbow.vm.Continuation;
import rainbow.vm.StackFrameElement;
import rainbow.Bindings;
import rainbow.ArcError;
import rainbow.types.ArcObject;
import rainbow.types.Pair;

import java.util.Map;
import java.util.HashMap;

public abstract class ContinuationSupport implements Continuation, Cloneable {
  public static final Map instances = new HashMap();
  protected ArcThread thread;
  protected Bindings namespace;
  protected Continuation whatToDo;
  protected boolean stopped;

  public ContinuationSupport(ArcThread thread, Bindings namespace, Continuation whatToDo) {
    this.thread = thread;
    this.namespace = namespace;
    this.whatToDo = whatToDo;
    increment(getClass());
  }

  private static void increment(Class aClass) {
    Integer i = (Integer) instances.get(aClass);
    if (i == null) {
      i = 0;
    }
    instances.put(aClass, i + 1);
  }

  public final void eat(ArcObject returned) {
    if (stopped) {
      return;
    }
    try {
      digest(returned);
    } catch (ArcError e) {
      error(e);
      throw e;
    } catch (Exception e) {
      ArcError ae = new ArcError(e);
      ae.addStackFrame(this, null);
      throw ae;
    }
  }

  protected abstract void digest(ArcObject returned);

  public void error(ArcError error) {
//    System.out.println("error " + error + " on " + this + "; current target is " + getCurrentTarget());
    ArcObject invocation = getCurrentTarget();
    if (invocation == null) {
      throw new NullPointerException("invocation must not be null in " + getClass());
    }
    error.addStackFrame(new StackFrameElement(this, Pair.buildFrom(invocation)));
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

