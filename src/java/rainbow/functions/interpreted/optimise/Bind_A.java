package rainbow.functions.interpreted.optimise;

import rainbow.ArcError;
import rainbow.LexicalClosure;
import rainbow.functions.interpreted.InterpretedFunction;
import rainbow.types.Pair;
import rainbow.types.ArcObject;
import rainbow.vm.VM;

import java.util.Map;

public class Bind_A extends InterpretedFunction {
  public Bind_A(ArcObject parameterList, Map lexicalBindings, Pair body) {
    super(parameterList, lexicalBindings, body);
  }

  public void invokeN(VM vm, LexicalClosure lc, ArcObject arg) {
    lc = new LexicalClosure(lexicalBindings.size(), lc);
    lc.add(arg);
    vm.pushFrame(lc, this.instructions);
  }

  public void invoke(VM vm, LexicalClosure lc, Pair args) {
    try {
      args.cdr().mustBeNil();
    } catch (NotNil notNil) {
      throw new ArcError("expected 1 arg, got extra " + args.cdr() + " calling " + this);
    }
    invokeN(vm, lc, args.car());
  }
}
