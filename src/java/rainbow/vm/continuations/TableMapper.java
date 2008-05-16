package rainbow.vm.continuations;

import rainbow.Function;
import rainbow.LexicalClosure;
import rainbow.types.ArcObject;
import rainbow.types.Hash;
import rainbow.types.Pair;
import rainbow.vm.ArcThread;
import rainbow.vm.Continuation;

public class TableMapper extends ContinuationSupport {
  private Function f;
  private Hash hash;
  private Pair pairs;

  public TableMapper(ArcThread thread, LexicalClosure lc, Continuation caller, Function f, Hash hash) {
    super(thread, lc, caller);
    this.f = f;
    this.hash = hash;
    this.pairs = hash.toList();
  }

  public void onReceive(ArcObject o) {
    if (pairs.isNil()) {
      caller.receive(hash);
    } else {
      Pair args = Pair.buildFrom(pairs.car().car(), pairs.car().cdr());
      pairs = (Pair) pairs.cdr();
      f.invoke(thread, lc, this, args);
    }
  }

  protected ArcObject getCurrentTarget() {
    return new Pair((ArcObject) f, hash);
  }

  public Continuation cloneFor(ArcThread thread) {
    TableMapper e = (TableMapper) super.cloneFor(thread);
    e.pairs = this.pairs.copy();
    return e;
  }
}
