package rainbow.vm.continuations;

import rainbow.LexicalClosure;
import rainbow.types.ArcObject;
import rainbow.types.Symbol;
import rainbow.vm.ArcThread;
import rainbow.vm.BoundSymbol;
import rainbow.vm.Continuation;

public class SetSetterContinuation extends ContinuationSupport {
  private ArcObject symbol;

  public SetSetterContinuation(ArcThread thread, LexicalClosure lc, Continuation caller, ArcObject symbol) {
    super(thread, lc, caller);
    this.symbol = symbol;
  }

  public void onReceive(ArcObject o) {
    if (symbol instanceof BoundSymbol) {
      ((BoundSymbol) symbol).set(lc, o);
    } else {
      Symbol global = Symbol.cast(symbol, this);
      global.setValue(o);
    }
    caller.receive(o);
  }

  protected ArcObject getCurrentTarget() {
    return symbol;
  }
}
