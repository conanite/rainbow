package rainbow.functions.eval;

import rainbow.functions.Builtin;
import rainbow.vm.VM;
import rainbow.types.Pair;
import rainbow.types.ArcObject;

import java.util.Map;
import java.util.List;
import java.util.ArrayList;

public class Eval extends Builtin {
  public Eval() {
    super("eval");
  }

  public void invoke(VM vm, Pair args) {
    ArcObject expression = args.car();
    expression = rainbow.vm.compiler.Compiler.compile(vm, expression, new Map[0]).reduce();
    List i = new ArrayList();
    expression.addInstructions(i);
    vm.pushInvocation(null, Pair.buildFrom(i));
  }
}
