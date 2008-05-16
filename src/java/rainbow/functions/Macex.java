package rainbow.functions;

import rainbow.Function;
import rainbow.LexicalClosure;
import rainbow.types.ArcObject;
import rainbow.types.Pair;
import rainbow.types.Symbol;
import rainbow.types.Tagged;
import rainbow.vm.ArcThread;
import rainbow.vm.Continuation;
import rainbow.vm.continuations.MacExpander;

public class Macex extends Builtin {
  public void invoke(final ArcThread thread, LexicalClosure lc, final Continuation whatToDo, final Pair args) {
    if (args.isNil()) {
      whatToDo.receive(args);
      return;
    }
    final ArcObject expression = args.car();
    if (!(expression instanceof Pair)) {
      whatToDo.receive(expression);
      return;
    }
    ArcObject macCall = expression.car();
    if (!(macCall instanceof Symbol)) {
      whatToDo.receive(expression);
      return;
    }
    Symbol macroName = (Symbol) macCall;
    ArcObject macro = thread.environment().lookup(macroName);
    if (macro == null) {
      whatToDo.receive(expression);
      return;
    }
    Function fn = (Function) Tagged.ifTagged(macro, "mac");
    if (fn == null) {
      whatToDo.receive(expression);
    } else {
      fn.invoke(thread, lc, new MacExpander(thread, lc, whatToDo, !args.cdr().car().isNil()), (Pair) expression.cdr());
    }
  }
}
