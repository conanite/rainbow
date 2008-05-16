package rainbow.functions;

import rainbow.vm.ArcThread;
import rainbow.vm.Continuation;
import rainbow.vm.continuations.TopLevelContinuation;
import rainbow.*;
import rainbow.types.Pair;
import rainbow.types.ArcNumber;
import rainbow.types.ArcObject;

public class Threads {
  public static class NewThread extends Builtin {
    public void invoke(ArcThread thread, final LexicalClosure lc, Continuation whatToDo, final Pair args) {
      final ArcThread newThread = new ArcThread(thread.environment());
      new Thread(new Runnable() {
        public void run() {
          Function fn = cast(args.car(), Function.class);
          fn.invoke(newThread, lc, new TopLevelContinuation(newThread), NIL);
          newThread.run();
        }
      }).start();
      whatToDo.receive(newThread);
    }
  }

  public static class Sleep extends Builtin {
    public void invoke(ArcThread thread, LexicalClosure lc, Continuation whatToDo, Pair args) {
      ArcNumber seconds = cast(args.car(), ArcNumber.class);
      try {
        Thread.sleep((long) (seconds.toDouble() * 1000));
      } catch (InterruptedException e) {
        throw new ArcError("sleep: thread interruped : " + e.getMessage(), e);
      }
    }
  }

  public static class Dead extends Builtin {
    public void invoke(ArcThread thread, LexicalClosure lc, Continuation whatToDo, Pair args) {
      ArcThread target = cast(args.car(), ArcThread.class);
      whatToDo.receive(Truth.valueOf(target.isDead()));
    }
  }

  public static class AtomicInvoke extends Builtin {
    private static final Object lock = new Object();

    public void invoke(ArcThread thread, LexicalClosure lc, Continuation whatToDo, Pair args) {
      synchronized (lock) {
        cast(args.car(), Function.class).invoke(thread, lc, whatToDo, NIL);
      }
    }
  }

  public static class ContinuationWrapper extends Builtin {
    private Continuation continuation;

    public ContinuationWrapper(Continuation continuation) {
      this.continuation = continuation.cloneFor(null);
    }

    public void invoke(ArcThread thread, LexicalClosure lc, Continuation deadContinuation, Pair args) {
      deadContinuation.stop();
      continuation.cloneFor(thread).receive(args.car());
    }
  }

  public static class Closure extends ArcObject implements Function {
    private Function expression;
    private LexicalClosure lc;

    public Closure(Function expression, LexicalClosure lc) {
      this.expression = expression;
      this.lc = lc;
    }

    public void invoke(ArcThread thread, LexicalClosure lc, Continuation whatToDo, Pair args) {
      expression.invoke(thread, this.lc, whatToDo, args);
    }

    public String code() {
      return expression.code();
    }

    public ArcObject type() {
      return Builtin.TYPE;
    }

    public String toString() {
      return expression.toString();
    }
  }

  public static class CCC extends Builtin {
    public void invoke(ArcThread thread, LexicalClosure lc, Continuation whatToDo, Pair args) {
      checkMaxArgCount(args, getClass(), 1);
      ContinuationWrapper e = new ContinuationWrapper(whatToDo);
      Function toCall = (Function) args.car();
      toCall.invoke(thread, lc, whatToDo, Pair.buildFrom(e));
    }
  }
}
