package rainbow.vm.compiler;

import rainbow.functions.Macex;
import rainbow.types.ArcObject;
import rainbow.types.Pair;
import rainbow.vm.Continuation;
import rainbow.vm.continuations.ContinuationSupport;

public class MacExpander extends ContinuationSupport {
  private final Macex macex = new Macex();
  private final boolean onlyOnce;

  public MacExpander(Continuation caller, boolean onlyOnce) {
    super(caller);
    this.onlyOnce = onlyOnce;
  }

  public void onReceive(ArcObject expansion) { // todo i think this is the wrong way around!
    if (onlyOnce) {
      caller.receive(expansion);
    } else {
      macex.invoke(lc, caller, Pair.buildFrom(expansion));
    }
  }
}
