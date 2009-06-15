package rainbow.vm.interpreter;

import rainbow.types.ArcObject;
import rainbow.types.Pair;
import rainbow.types.Symbol;
import rainbow.vm.ArcThread;
import rainbow.vm.Continuation;
import rainbow.vm.continuations.FunctionDispatcher;
import rainbow.LexicalClosure;

public class Invocation extends ArcObject {
  private Pair invocation;

  public Invocation(Pair invocation) {
    this.invocation = invocation;
  }

  public void interpret(ArcThread thread, LexicalClosure lc, Continuation caller) {
    thread.continueWith(new FunctionDispatcher(thread, lc, caller, invocation));
  }

  public ArcObject type() {
    return Symbol.make("function-invocation");
  }

  public String toString() {
    return invocation.toString();
  }
}
