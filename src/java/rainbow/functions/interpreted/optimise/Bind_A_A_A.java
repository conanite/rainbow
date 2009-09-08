package rainbow.functions.interpreted.optimise;

import rainbow.LexicalClosure;
import rainbow.functions.interpreted.InterpretedFunction;
import rainbow.types.ArcObject;
import rainbow.types.Pair;
import rainbow.vm.VM;

import java.util.Map;

public class Bind_A_A_A extends InterpretedFunction {
  public Bind_A_A_A(ArcObject parameterList, Map lexicalBindings, Pair body) {
    super(parameterList, lexicalBindings, body);
  }

  public void invokeN(VM vm, LexicalClosure lc, ArcObject arg1, ArcObject arg2, ArcObject arg3) {
    lc = new LexicalClosure(lexicalBindings.size(), lc);
    lc.add(arg1);
    lc.add(arg2);
    lc.add(arg3);
    vm.pushInvocation(lc, this.instructions);
  }

  public void invoke(VM vm, LexicalClosure lc, Pair args) {
    requireNil(args.cdr().cdr().cdr(), args);
    invokeN(vm, lc, args.car(), args.cdr().car(), args.cdr().cdr().car());
  }
}
