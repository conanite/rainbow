package rainbow.vm.continuations;

import rainbow.Function;
import rainbow.Bindings;
import rainbow.ArcError;
import rainbow.types.ArcObject;
import rainbow.vm.ArcThread;
import rainbow.vm.Continuation;

public class Protector extends ContinuationSupport {
  private final Function after;

  public Protector(ArcThread thread, Bindings namespace, Continuation whatToDo, Function after) {
    super(thread, namespace, whatToDo);
    this.after = after;
  }

  public void digest(final ArcObject result) {
    after.invoke(thread, namespace, new ResultPassingContinuation(whatToDo, result), ArcObject.NIL);
  }

  public void error(final ArcError originalError) {
    after.invoke(thread, namespace, new ErrorPassingContinuation(whatToDo, originalError), ArcObject.NIL);
  }
}
