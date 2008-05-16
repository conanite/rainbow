package rainbow.vm.continuations;

import rainbow.LexicalClosure;
import rainbow.types.ArcObject;
import rainbow.vm.Continuation;
import rainbow.vm.ArcThread;
import rainbow.vm.Interpreter;

public class SetContinuation extends ContinuationSupport {
  private ArcObject value;

  public SetContinuation(ArcThread thread, LexicalClosure lc, Continuation caller, ArcObject value) {
    super(thread, lc, caller);
    this.value = value;
  }

  public void onReceive(ArcObject name) {
    Interpreter.interpret(thread, lc, new SetSetterContinuation(thread, lc, caller, name), value);
  }

  protected ArcObject getCurrentTarget() {
    return value;
  }
}
