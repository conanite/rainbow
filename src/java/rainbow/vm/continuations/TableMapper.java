package rainbow.vm.continuations;

import rainbow.vm.Continuation;
import rainbow.vm.ArcThread;
import rainbow.Function;
import rainbow.Bindings;
import rainbow.types.Hash;
import rainbow.types.Pair;
import rainbow.types.ArcObject;

public class TableMapper extends ContinuationSupport {
  private Function f;
  private Hash hash;
  private Pair pairs;

  public TableMapper(ArcThread thread, Bindings namespace, Continuation whatToDo, Function f, Hash hash) {
    super(thread, namespace, whatToDo);
    this.f = f;
    this.hash = hash;
    this.pairs = hash.toList();
  }

  public void digest(ArcObject o) {
    if (pairs.isNil()) {
      whatToDo.eat(hash);
    } else {
      Pair args = Pair.buildFrom(pairs.car().car(), pairs.car().cdr());
      pairs = (Pair) pairs.cdr();
      f.invoke(thread, namespace, this, args);
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
