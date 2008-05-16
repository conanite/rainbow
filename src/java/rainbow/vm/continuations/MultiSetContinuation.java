package rainbow.vm.continuations;

import rainbow.LexicalClosure;
import rainbow.functions.Macex;
import rainbow.types.ArcObject;
import rainbow.types.Pair;
import rainbow.vm.ArcThread;
import rainbow.vm.Continuation;

public class MultiSetContinuation extends ContinuationSupport {
  private static final Macex MACEX = new Macex();
  private Pair args;

  public MultiSetContinuation(ArcThread thread, LexicalClosure lc, Continuation caller, Pair args) {
    super(thread, lc, caller);
    this.args = args;
  }

  public void start() {
    ArcObject name = args.car();
    ArcObject value = args.cdr().car();
    args = (Pair) args.cdr().cdr();
    MACEX.invoke(thread, lc, new SetContinuation(thread, lc, this, value), Pair.buildFrom(name));
  }

  public void onReceive(ArcObject o) {
    if (args.isNil()) {
      caller.receive(o);
    } else {
      start();
    }
  }

  public Continuation cloneFor(ArcThread thread) {
    MultiSetContinuation e = (MultiSetContinuation) super.cloneFor(thread);
    e.args = this.args.copy();
    return e;
  }
}
