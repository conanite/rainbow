package rainbow.vm.continuations;

import rainbow.ArcError;
import rainbow.Function;
import rainbow.LexicalClosure;
import rainbow.types.ArcException;
import rainbow.types.ArcObject;
import rainbow.types.Pair;
import rainbow.vm.ArcThread;
import rainbow.vm.Continuation;

public class ErrorHandler extends ContinuationSupport {
  private final Function errorHandler;

  public ErrorHandler(ArcThread thread, LexicalClosure lc, Continuation caller, Function errorHandler) {
    super(thread, lc, caller);
    this.errorHandler = errorHandler;
  }

  public void onReceive(ArcObject o) {
    caller.receive(o);
  }

  public void error(ArcError error) {
    errorHandler.invoke(thread, lc, caller, Pair.buildFrom(new ArcException(error)));
  }
}
