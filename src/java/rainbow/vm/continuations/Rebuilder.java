package rainbow.vm.continuations;

import rainbow.types.ArcObject;
import rainbow.types.Pair;
import rainbow.vm.ArcThread;
import rainbow.vm.Continuation;
import rainbow.Bindings;

public class Rebuilder extends ContinuationSupport {
  private ArcObject car;

  public Rebuilder(ArcThread thread, Bindings namespace, Continuation whatToDo, ArcObject car) {
    super(thread, namespace, whatToDo);
    this.car = car;
  }

  protected void digest(ArcObject returned) {
    whatToDo.eat(Pair.buildFrom(car, returned));
  }
}
