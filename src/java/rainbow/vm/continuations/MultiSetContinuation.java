package rainbow.vm.continuations;

import rainbow.vm.Continuation;
import rainbow.vm.ArcThread;
import rainbow.types.ArcObject;
import rainbow.types.Pair;
import rainbow.functions.Macex;
import rainbow.Bindings;

public class MultiSetContinuation extends ContinuationSupport {
  private static final Macex MACEX = new Macex();
  private Pair originalArgs;
  private Pair args;

  public MultiSetContinuation(ArcThread thread, Bindings namespace, Continuation whatToDo, Pair args) {
    super(thread, namespace, whatToDo);
    this.args = args;
    this.originalArgs = args;
  }

  public void start() {
    ArcObject name = args.car();
    ArcObject value = args.cdr().car();
    args = (Pair) args.cdr().cdr();
    MACEX.invoke(thread, namespace, new SetContinuation(thread, namespace, this, value), Pair.buildFrom(name));
  }

  public void digest(ArcObject o) {
    if (args.isNil()) {
      whatToDo.eat(o);
    } else {
      start();
    }
  }

  protected ArcObject getCurrentTarget() {
    return originalArgs;
  }

  public Continuation cloneFor(ArcThread thread) {
    MultiSetContinuation e = (MultiSetContinuation) super.cloneFor(thread);
    e.args = this.args.copy();
    return e;
  }
}
