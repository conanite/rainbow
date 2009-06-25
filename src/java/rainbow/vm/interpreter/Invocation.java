package rainbow.vm.interpreter;

import rainbow.ArcError;
import rainbow.LexicalClosure;
import rainbow.types.ArcObject;
import rainbow.types.Pair;
import rainbow.types.Symbol;
import rainbow.vm.Continuation;
import rainbow.vm.continuations.InvocationContinuation;
import rainbow.vm.interpreter.invocation.*;

public class Invocation extends ArcObject {
  private InvocationComponent invocationComponent;

  public void interpret(LexicalClosure lc, Continuation caller) {
    caller.thread().continueWith(new InvocationContinuation(lc, caller, invocationComponent));
  }

  public void buildFrom(Pair args) {
    switch ((int) args.len()) {
      case 0:
        throw new ArcError("Invocation: internal error: empty list: not allowed!");
      case 1:
        invocationComponent = new NoArgs();
        break;
      case 2:
        invocationComponent = new FunctionInvocation();
        invocationComponent.add(new SingleArg());
        break;
      default:
        invocationComponent = new FunctionInvocation();
        invocationComponent.add(new FirstArg());
        for (int i = 0; i < args.len() - 3; i++) {
          invocationComponent.add(new IntermediateArg());
        }
        invocationComponent.add(new LastArg());
    }

    while (!args.isNil()) {
      invocationComponent.take(args.car());
      args = (Pair) args.cdr();
    }
  }

  public ArcObject type() {
    return Symbol.make("function-invocation");
  }
}
