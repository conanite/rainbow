package rainbow.vm.continuations;

import rainbow.vm.ArcThread;
import rainbow.vm.Continuation;
import rainbow.types.ArcObject;
import rainbow.types.Pair;

import java.util.List;
import java.util.LinkedList;

public class UnquoteSplicer extends ContinuationSupport {
  private List<ArcObject> result;

  public UnquoteSplicer(Continuation caller, List<ArcObject> result) {
    super(null, null, caller);
    this.result = result;
  }

  public void onReceive(ArcObject o) {
    ((Pair)o).copyTo(result);
    caller.receive(null);
  }

  public Continuation cloneFor(ArcThread thread) {
    UnquoteSplicer e = (UnquoteSplicer) super.cloneFor(thread);
    e.result = new LinkedList<ArcObject>(result);
    return e;
  }
}
