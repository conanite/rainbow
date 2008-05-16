package rainbow.vm.continuations;

import rainbow.ArcError;
import rainbow.Function;
import rainbow.LexicalClosure;
import rainbow.types.ArcObject;
import rainbow.vm.ArcThread;
import rainbow.vm.Continuation;

public class Protector extends ContinuationSupport {
  private final Function after;

  public Protector(ArcThread thread, LexicalClosure lc, Continuation caller, Function after) {
    super(thread, lc, caller);
    this.after = after;
  }

  public void onReceive(final ArcObject result) {
    after.invoke(thread, lc, new ResultPassingContinuation(caller, result), ArcObject.NIL);
  }

  public void error(final ArcError originalError) {
    after.invoke(thread, lc, new ErrorPassingContinuation(caller, originalError), ArcObject.NIL);
  }
}
