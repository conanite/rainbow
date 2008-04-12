package rainbow.vm.continuations;

import rainbow.vm.Continuation;
import rainbow.types.ArcObject;
import rainbow.types.Symbol;
import rainbow.Bindings;
import rainbow.functions.Builtin;

public class SetSetterContinuation extends ContinuationSupport {
  private ArcObject symbol;

  public SetSetterContinuation(Bindings namespace, Continuation whatToDo, ArcObject symbol) {
    super(null, namespace, whatToDo);
    this.symbol = symbol;
  }

  public void digest(ArcObject o) {
    String name = Builtin.cast(symbol, Symbol.class).name();
    namespace.addToNamespace(name, o);
    whatToDo.eat(o);
  }

  protected ArcObject getCurrentTarget() {
    return symbol;
  }
}
