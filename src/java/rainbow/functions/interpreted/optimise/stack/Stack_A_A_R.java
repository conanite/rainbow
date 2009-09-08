package rainbow.functions.interpreted.optimise.stack;

import rainbow.LexicalClosure;
import rainbow.functions.interpreted.InterpretedFunction;
import rainbow.functions.interpreted.StackFunctionSupport;
import rainbow.types.ArcObject;
import rainbow.types.Pair;
import rainbow.vm.VM;

import java.util.Map;

public class Stack_A_A_R extends StackFunctionSupport {
  public Stack_A_A_R(InterpretedFunction original) {
    super(original.parameterList(), original.lexicalBindings, convert(original.lexicalBindings, original.body));
  }

  public Stack_A_A_R(ArcObject parameterList, Map lexicalBindings, Pair body) {
    super(parameterList, lexicalBindings, body);
  }

  public void invoke(VM vm, LexicalClosure lc, Pair args) {
    vm.pushInvocation(lc, this.instructions, new ArcObject[] { args.car(), args.cdr().car(), args.cdr().cdr() });
  }
}
