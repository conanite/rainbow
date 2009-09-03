package rainbow.functions.interpreted.optimise;

import rainbow.ArcError;
import rainbow.LexicalClosure;
import rainbow.Nil;
import rainbow.functions.interpreted.InterpretedFunction;
import rainbow.types.ArcObject;
import rainbow.types.Pair;
import rainbow.vm.VM;

import java.util.Map;

public class Bind_A extends InterpretedFunction {
  public Bind_A(ArcObject parameterList, Map lexicalBindings, Pair body) {
    super(parameterList, lexicalBindings, body);
  }

  public void invokeN(VM vm, LexicalClosure lc) {
    throw new ArcError("function " + this + " expects 1 arg, got none");
  }

  public void invokeN(VM vm, LexicalClosure lc, ArcObject arg) {
    lc = new LexicalClosure(lexicalBindings.size(), lc);
    lc.add(arg);
    vm.pushFrame(lc, this.instructions);
  }

  public void invoke(VM vm, LexicalClosure lc, Pair args) {
    if (args instanceof Nil) {
      invokeN(vm, lc);
    }
    try {
      args.cdr().mustBeNil();
    } catch (NotNil notNil) {
      throw new ArcError("expected 1 arg, got " + args + " calling " + this);
    }
    invokeN(vm, lc, args.car());
  }
}
