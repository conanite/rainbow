package rainbow.functions;

import rainbow.*;
import rainbow.types.ArcNumber;
import rainbow.types.ArcObject;
import rainbow.types.Pair;
import rainbow.vm.ArcThread;
import rainbow.vm.Continuation;
import rainbow.vm.continuations.Atomic;
import rainbow.vm.continuations.TopLevelContinuation;

public class Threads {
  public static void collect(Environment top) {
    top.add(new Builtin[]{
      new Builtin("new-thread") {
        public void invoke(final LexicalClosure lc, Continuation caller, final Pair args) {
          final ArcThread newThread = new ArcThread();
          new Thread() {
            public void run() {
              Function fn = Builtin.cast(args.car(), this);
              fn.invoke(lc, new TopLevelContinuation(newThread), NIL);
              newThread.run();
            }
          }.start();
          caller.receive(newThread);
        }
      }, new Builtin("kill-thread") {
        public ArcObject invoke(Pair args) {
          ArcThread.cast(args.car(), this).stop();
          return NIL;
        }
      }, new Builtin("sleep") {
        public ArcObject invoke(Pair args) {
          ArcNumber seconds = ArcNumber.cast(args.car(), this);
          try {
            Thread.sleep((long) (seconds.toDouble() * 1000));
          } catch (InterruptedException e) {
            throw new ArcError("sleep: thread interruped : " + e.getMessage(), e);
          }
          return NIL;
        }
      }, new Builtin("dead") {
        public void invoke(LexicalClosure lc, Continuation caller, Pair args) {
          ArcThread target = ArcThread.cast(args.car(), this);
          caller.receive(Truth.valueOf(target.isDead()));
        }
      }, new Builtin("atomic-invoke") {
        public void invoke(LexicalClosure lc, Continuation caller, Pair args) {
          Builtin.cast(args.car(), this).invoke(lc, new Atomic(lc, caller), NIL);
        }
      }, new Builtin("ccc") {
        public void invoke(LexicalClosure lc, Continuation caller, Pair args) {
          checkMaxArgCount(args, getClass(), 1);
          ContinuationWrapper e = new ContinuationWrapper(caller);
          args.car().invoke(lc, caller, Pair.buildFrom(e));
        }
      }
    });
  }

  public static class ContinuationWrapper extends Builtin {
    private Continuation continuation;

    public ContinuationWrapper(Continuation continuation) {
      this.continuation = continuation.cloneFor(null);
    }

    public void invoke(LexicalClosure lc, Continuation deadContinuation, Pair args) {
      deadContinuation.stop();
      continuation.cloneFor(deadContinuation.thread()).receive(args.car());
    }
  }
}
