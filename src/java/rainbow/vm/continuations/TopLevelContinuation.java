package rainbow.vm.continuations;

import rainbow.vm.ArcThread;
import rainbow.types.ArcObject;
import rainbow.ArcError;

public class TopLevelContinuation extends ContinuationSupport {
  public TopLevelContinuation(ArcThread thread) {
    super(thread, null, null);
  }

  public void onReceive(ArcObject o) {
    thread.finalValue(o);
  }

  public void error(ArcError error) {
    thread.error(error);
  }

  public void stop() {
    this.stopped = true;
  }
}
