package rainbow.vm.continuations;

import rainbow.vm.Continuation;
import rainbow.types.ArcObject;

public class ResultPassingContinuation extends ContinuationSupport {
  private final ArcObject result;

  public ResultPassingContinuation(Continuation whatToDo, ArcObject result) {
    super(null, null, whatToDo);
    this.result = result;
  }

  public void digest(ArcObject ignore) {
    whatToDo.eat(result);
  }

  protected ArcObject getCurrentTarget() {
    return result;
  }
}
