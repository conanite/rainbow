package rainbow.vm.continuations;

import rainbow.vm.Continuation;
import rainbow.ArcError;
import rainbow.types.ArcObject;

public class ErrorPassingContinuation extends ContinuationSupport {
  private final ArcError originalError;

  public ErrorPassingContinuation(Continuation whatToDo, ArcError originalError) {
    super(null, null, whatToDo);
    this.originalError = originalError;
  }

  public void digest(ArcObject o) {
    whatToDo.error(originalError);
  }

  public void error(ArcError error) {
    whatToDo.error(originalError);
  }
}
