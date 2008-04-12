package rainbow.vm.continuations;

import rainbow.vm.Continuation;
import rainbow.vm.ArcThread;
import rainbow.Function;
import rainbow.Bindings;
import rainbow.types.ArcObject;
import rainbow.types.Pair;

public class FunctionInvoker extends ContinuationSupport {
  private final Function function;

  public FunctionInvoker(ArcThread thread, Bindings namespace, Continuation whatToDo, Function function) {
    super(thread, namespace, whatToDo);
    this.function = function;
  }

  public void digest(ArcObject o) {
    function.invoke(thread, namespace, whatToDo, (Pair)o);
  }

  protected ArcObject getCurrentTarget() {
    return (ArcObject) function;
  }

  public String toString() {
    return "FunctionInvoker for " + function + "; to return to " + whatToDo;
  }
}
