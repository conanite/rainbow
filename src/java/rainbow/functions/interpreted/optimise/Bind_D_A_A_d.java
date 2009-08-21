package rainbow.functions.interpreted.optimise;

import rainbow.LexicalClosure;
import rainbow.functions.interpreted.InterpretedFunction;
import rainbow.types.ArcObject;
import rainbow.types.Pair;
import rainbow.vm.VM;

import java.util.Map;

public class Bind_D_A_A_d extends InterpretedFunction {
  public Bind_D_A_A_d(ArcObject parameterList, Map lexicalBindings, Pair body) {
    super(parameterList, lexicalBindings, body);
  }

  public void invokeN(VM vm, LexicalClosure lc, ArcObject arg) {
    lc = new LexicalClosure(lexicalBindings.size(), lc);

    Pair destructured = (Pair) arg;
    lc.add(destructured.car());

    destructured = (Pair) destructured.cdr();
    lc.add(destructured.car());

    vm.pushFrame(lc, this.instructions);
  }

  public void invoke(VM vm, LexicalClosure lc, Pair args) {
    requireNil(args.cdr(), args);
    invokeN(vm, lc, args.car());
  }
}
