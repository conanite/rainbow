package rainbow.functions.interpreted.optimise;

import rainbow.functions.interpreted.InterpretedFunction;
import rainbow.types.Pair;
import rainbow.types.ArcObject;
import rainbow.vm.VM;
import rainbow.LexicalClosure;
import rainbow.ArcError;

import java.util.Map;

public class Bind_A_A extends InterpretedFunction {
  public Bind_A_A(ArcObject parameterList, Map lexicalBindings, Pair body) {
    super(parameterList, lexicalBindings, body);
  }

  public void invokeN(VM vm, LexicalClosure lc) {
    throw new ArcError("error: expected 2 args, got none");
  }

  public void invokeN(VM vm, LexicalClosure lc, ArcObject arg) {
    throw new ArcError("error: expected 2 args, got 1: " + arg);
  }

  public void invokeN(VM vm, LexicalClosure lc, ArcObject arg1, ArcObject arg2) {
    lc = new LexicalClosure(lexicalBindings.size(), lc);
    lc.add(arg1);
    lc.add(arg2);
    vm.pushInvocation(lc, this.instructions);
  }

  public void invoke(VM vm, LexicalClosure lc, Pair args) {
    try {
      args.cdr().cdr().mustBeNil();
    } catch (NotNil notNil) {
      throw new ArcError("expected 2 args, got " + args);
    }
    invokeN(vm, lc, args.car(), args.cdr().car());
  }
}
