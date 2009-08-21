package rainbow.functions;

import rainbow.Nil;
import rainbow.types.ArcObject;
import rainbow.types.Pair;
import rainbow.types.Symbol;
import rainbow.types.Tagged;
import rainbow.vm.VM;

public class Macex extends Builtin {
  public Macex() {
    super("macex");
  }

  public void invoke(VM vm, final Pair args) {
    if (args instanceof Nil) {
      vm.pushA(NIL);
      return;
    }

    ArcObject expression = args.car();
    if (!(expression instanceof Pair)) {
      vm.pushA(expression);
      return;
    }

    ArcObject macCall = expression.car();
    if (!(macCall instanceof Symbol)) {
      vm.pushA(expression);
      return;
    }

    Symbol macroName = (Symbol) macCall;
    if (!macroName.bound()) {
      vm.pushA(expression);
      return;
    }

    ArcObject macro = macroName.value();
    ArcObject fn = Tagged.ifTagged(macro, "mac");
    if (fn == null) {
      vm.pushA(expression);
      return;
    }

    expression = fn.invokeAndWait(vm, (Pair) expression.cdr());
    invoke(vm, new Pair(expression, NIL));
  }
}
