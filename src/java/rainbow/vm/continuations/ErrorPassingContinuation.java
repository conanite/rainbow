package rainbow.vm.continuations;

import rainbow.vm.Continuation;
import rainbow.ArcError;
import rainbow.types.ArcObject;

public class ErrorPassingContinuation extends ContinuationSupport {
  private final ArcError originalError;

  public ErrorPassingContinuation(Continuation caller, ArcError originalError) {
    super(null, null, caller);
    this.originalError = originalError;
  }

  public void onReceive(ArcObject o) {
    caller.error(originalError);
  }

  public void error(ArcError error) {
    caller.error(originalError);
  }
}
