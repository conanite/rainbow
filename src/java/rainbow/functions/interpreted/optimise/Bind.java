package rainbow.functions.interpreted.optimise;

import rainbow.ArcError;
import rainbow.LexicalClosure;
import rainbow.functions.interpreted.InterpretedFunction;
import rainbow.types.Pair;
import rainbow.types.ArcObject;
import rainbow.vm.VM;

import java.util.Map;

public class Bind extends InterpretedFunction {
  public Bind(ArcObject parameterList, Map lexicalBindings, Pair body) {
    super(parameterList, lexicalBindings, body);
  }

  public void invokeN(VM vm, LexicalClosure lc) {
    vm.pushInvocation(lc, this.instructions);
  }

  public void invoke(VM vm, LexicalClosure lc, Pair args) {
    try {
      args.mustBeNil();
    } catch (NotNil notNil) {
      throw new ArcError("expected 1 arg, got " + args);
    }
    vm.pushInvocation(lc, this.instructions);
  }
}
