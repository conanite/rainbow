package rainbow.vm.continuations;

import rainbow.vm.Continuation;
import rainbow.vm.ArcThread;
import rainbow.functions.Macex;
import rainbow.Bindings;
import rainbow.types.ArcObject;
import rainbow.types.Pair;

public class MacExpander extends ContinuationSupport {
  private final Macex macex = new Macex();
  private final boolean onlyOnce;

  public MacExpander(ArcThread thread, Bindings namespace, Continuation whatToDo, boolean onlyOnce) {
    super(thread, namespace, whatToDo);
    this.onlyOnce = onlyOnce;
  }

  public void digest(ArcObject expansion) { // todo i think this is the wrong way around!
    if (onlyOnce) {
      whatToDo.eat(expansion);
    } else {
      macex.invoke(thread, namespace, whatToDo, Pair.buildFrom(expansion)); // todo move macex.invoke into start() ??
    }
  }
}
