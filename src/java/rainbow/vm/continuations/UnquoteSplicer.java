package rainbow.vm.continuations;

import rainbow.types.ArcObject;
import rainbow.vm.ArcThread;
import rainbow.vm.Continuation;

import java.util.LinkedList;
import java.util.List;

public class UnquoteSplicer extends ContinuationSupport {
  private List<ArcObject> result;

  public UnquoteSplicer(Continuation caller, List<ArcObject> result) {
    super(null, null, caller);
    this.result = result;
  }

  public void onReceive(ArcObject o) {
    o.copyTo(result);
    caller.receive(null);
  }

  public Continuation cloneFor(ArcThread thread) {
    UnquoteSplicer e = (UnquoteSplicer) super.cloneFor(thread);
    e.result = new LinkedList<ArcObject>(result);
    return e;
  }
}
