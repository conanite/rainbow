package rainbow.functions.interpreted.optimise.stack;

import rainbow.LexicalClosure;
import rainbow.functions.interpreted.InterpretedFunction;
import rainbow.functions.interpreted.StackFunctionSupport;
import rainbow.types.ArcObject;
import rainbow.types.Pair;
import rainbow.vm.VM;

import java.util.Map;

public class Stack_D_A_A_A_A_d extends StackFunctionSupport {
  public Stack_D_A_A_A_A_d(InterpretedFunction original) {
    super(original.parameterList(), original.lexicalBindings, convert(original.lexicalBindings, original.body));
  }

  public Stack_D_A_A_A_A_d(ArcObject parameterList, Map lexicalBindings, Pair body) {
    super(parameterList, lexicalBindings, body);
  }

  public void invokef(VM vm, ArcObject arg) {
    vm.pushInvocation(null, this.instructions,
            new ArcObject[] {
                    arg.car(),
                    arg.cdr().car(),
                    arg.cdr().cdr().car(),
                    arg.cdr().cdr().cdr().car()
            });
  }

  public void invokeN(VM vm, LexicalClosure lc, ArcObject arg) {
    vm.pushInvocation(lc, this.instructions,
            new ArcObject[] {
                    arg.car(),
                    arg.cdr().car(),
                    arg.cdr().cdr().car(),
                    arg.cdr().cdr().cdr().car()
            });
  }

  public void invoke(VM vm, Pair args) {
    checkArgsLength(1, args);
    invokef(vm, args.car());
  }

  public void invoke(VM vm, LexicalClosure lc, Pair args) {
    checkArgsLength(1, args);
    invokeN(vm, lc, args.car());
  }
}
