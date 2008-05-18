package rainbow.vm.continuations;

import rainbow.LexicalClosure;
import rainbow.functions.Builtin;
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
      thread.environment().addToNamespace(Symbol.cast(symbol, this), o);
    }
    caller.receive(o);
  }

  protected ArcObject getCurrentTarget() {
    return symbol;
  }
}
