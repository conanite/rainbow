package rainbow.vm.continuations;

import rainbow.vm.Continuation;
import rainbow.types.ArcObject;

public class ResultPassingContinuation extends ContinuationSupport {
  private final ArcObject result;

  public ResultPassingContinuation(Continuation caller, ArcObject result) {
    super(caller);
    this.result = result;
  }

  public void onReceive(ArcObject ignore) {
    caller.receive(result);
  }

  protected ArcObject getCurrentTarget() {
    return result;
  }
}
