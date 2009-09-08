package rainbow.functions.interpreted.optimise.stack;

import rainbow.LexicalClosure;
import rainbow.functions.interpreted.InterpretedFunction;
import rainbow.functions.interpreted.StackFunctionSupport;
import rainbow.types.ArcObject;
import rainbow.types.Pair;
import rainbow.vm.VM;

import java.util.Map;

public class Stack_A_A_A extends StackFunctionSupport {
  public Stack_A_A_A(InterpretedFunction original) {
    super(original.parameterList(), original.lexicalBindings, convert(original.lexicalBindings, original.body));
  }

  public Stack_A_A_A(ArcObject parameterList, Map lexicalBindings, Pair body) {
    super(parameterList, lexicalBindings, body);
  }

  public void invokef(VM vm, ArcObject arg1, ArcObject arg2, ArcObject arg3) {
    vm.pushInvocation(null, this.instructions, new ArcObject[] { arg1, arg2, arg3 });
  }

  public void invokeN(VM vm, LexicalClosure lc, ArcObject arg1, ArcObject arg2, ArcObject arg3) {
    vm.pushInvocation(lc, this.instructions, new ArcObject[] { arg1, arg2, arg3 });
  }

  public void invoke(VM vm, Pair args) {
    checkArgsLength(3, args);
    invokef(vm, args.car(), args.cdr().car(), args.cdr().cdr().car());
  }

  public void invoke(VM vm, LexicalClosure lc, Pair args) {
    checkArgsLength(3, args);
    invokeN(vm, lc, args.car(), args.cdr().car(), args.cdr().cdr().car());
  }
}
