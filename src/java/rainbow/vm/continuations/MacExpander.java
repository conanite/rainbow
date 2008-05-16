package rainbow.vm.continuations;

import rainbow.LexicalClosure;
import rainbow.functions.Macex;
import rainbow.types.ArcObject;
import rainbow.types.Pair;
import rainbow.vm.ArcThread;
import rainbow.vm.Continuation;

public class MacExpander extends ContinuationSupport {
  private final Macex macex = new Macex();
  private final boolean onlyOnce;

  public MacExpander(ArcThread thread, LexicalClosure lc, Continuation caller, boolean onlyOnce) {
    super(thread, lc, caller);
    this.onlyOnce = onlyOnce;
  }

  public void onReceive(ArcObject expansion) { // todo i think this is the wrong way around!
    if (onlyOnce) {
      caller.receive(expansion);
    } else {
      macex.invoke(thread, lc, caller, Pair.buildFrom(expansion)); // todo move macex.invoke into start() ??
    }
  }
}
