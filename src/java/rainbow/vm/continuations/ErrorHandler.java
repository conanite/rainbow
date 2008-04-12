package rainbow.vm.continuations;

import rainbow.Bindings;
import rainbow.ArcError;
import rainbow.Function;
import rainbow.types.ArcException;
import rainbow.types.ArcObject;
import rainbow.types.Pair;
import rainbow.vm.ArcThread;
import rainbow.vm.Continuation;

public class ErrorHandler extends ContinuationSupport {
  private final Function errorHandler;

  public ErrorHandler(ArcThread thread, Bindings namespace, Continuation whatToDo, Function errorHandler) {
    super(thread, namespace, whatToDo);
    this.errorHandler = errorHandler;
  }

  public void digest(ArcObject o) {
    whatToDo.eat(o);
  }

  public void error(ArcError error) {
    errorHandler.invoke(thread, namespace, whatToDo, Pair.buildFrom(new ArcException(error)));
  }
}
