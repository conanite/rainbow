package rainbow.functions;

import rainbow.Function;
import rainbow.LexicalClosure;
import rainbow.types.ArcObject;
import rainbow.types.Pair;
import rainbow.types.Symbol;
import rainbow.types.Tagged;
import rainbow.vm.ArcThread;
import rainbow.vm.Continuation;
import rainbow.vm.compiler.MacExpander;

public class Macex extends Builtin {
  public void invoke(final ArcThread thread, LexicalClosure lc, final Continuation caller, final Pair args) {
    if (args.isNil()) {
      caller.receive(args);
      return;
    }
    final ArcObject expression = args.car();
    if (!(expression instanceof Pair)) {
      caller.receive(expression);
      return;
    }
    ArcObject macCall = expression.car();
    if (!(macCall instanceof Symbol)) {
      caller.receive(expression);
      return;
    }
    Symbol macroName = (Symbol) macCall;
    if (!macroName.bound()) {
      caller.receive(expression);
      return;
    }
    ArcObject macro = macroName.value();
    Function fn = (Function) Tagged.ifTagged(macro, "mac");
    if (fn == null) {
      caller.receive(expression);
    } else {
      fn.invoke(thread, lc, new MacExpander(thread, lc, caller, !args.cdr().car().isNil()), (Pair) expression.cdr());
    }
  }
}
