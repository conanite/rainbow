package rainbow.vm.continuations;

import rainbow.LexicalClosure;
import rainbow.types.ArcObject;
import rainbow.vm.ArcThread;
import rainbow.vm.Continuation;
import rainbow.vm.Interpreter;

public class SetContinuation extends ContinuationSupport {
  private ArcObject value;
  private ArcObject name;
  private boolean expectingValue;

  public SetContinuation(ArcThread thread, LexicalClosure lc, Continuation caller, ArcObject value) {
    super(thread, lc, caller);
    this.value = value;
  }

  public void onReceive(ArcObject obj) {
    if (expectingValue) {
      name.setSymbolValue(lc, obj);
      caller.receive(obj);
    } else {
      this.name = obj;
      expectingValue = true;
      Interpreter.interpret(thread, lc, this, value);
    }
  }

  protected ArcObject getCurrentTarget() {
    return value;
  }
}
