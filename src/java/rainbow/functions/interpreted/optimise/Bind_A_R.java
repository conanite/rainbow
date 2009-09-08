package rainbow.functions.interpreted.optimise;

import rainbow.ArcError;
import rainbow.LexicalClosure;
import rainbow.functions.interpreted.InterpretedFunction;
import rainbow.types.Pair;
import rainbow.types.ArcObject;
import rainbow.vm.VM;

import java.util.Map;

public class Bind_A_R extends InterpretedFunction {
  public Bind_A_R(ArcObject parameterList, Map lexicalBindings, Pair body) {
    super(parameterList, lexicalBindings, body);
  }

  public void invokeN(VM vm, LexicalClosure lc) {
    throw new ArcError("error: expected at least 1 arg, got none");
  }

  public void invoke(VM vm, LexicalClosure lc, Pair args) {
    lc = new LexicalClosure(lexicalBindings.size(), lc);
    lc.add(args.car());
    lc.add(args.cdr());
    vm.pushInvocation(lc, this.instructions);
  }
}
