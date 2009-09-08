package rainbow.functions.interpreted.optimise;

import rainbow.LexicalClosure;
import rainbow.functions.interpreted.InterpretedFunction;
import rainbow.types.ArcObject;
import rainbow.types.Pair;
import rainbow.vm.VM;

import java.util.Map;

public class Bind_R extends InterpretedFunction {
  public Bind_R(ArcObject parameterList, Map lexicalBindings, Pair body) {
    super(parameterList, lexicalBindings, body);
  }

  public void invoke(VM vm, LexicalClosure lc, Pair args) {
    lc = new LexicalClosure(lexicalBindings.size(), lc);
    lc.add(args);
    vm.pushInvocation(lc, this.instructions);
  }
}
