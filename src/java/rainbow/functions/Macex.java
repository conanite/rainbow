package rainbow.functions;

import rainbow.Bindings;
import rainbow.Function;
import rainbow.types.ArcObject;
import rainbow.types.Pair;
import rainbow.types.Symbol;
import rainbow.types.Tagged;
import rainbow.vm.ArcThread;
import rainbow.vm.Continuation;
import rainbow.vm.continuations.MacExpander;

public class Macex extends Builtin {
  public void invoke(final ArcThread thread, final Bindings namespace, final Continuation whatToDo, final Pair args) {
    if (args.isNil()) {
      whatToDo.eat(args);
      return;
    }
    final ArcObject expression = args.car();
    if (!(expression instanceof Pair)) {
      whatToDo.eat(expression);
      return;
    }
    ArcObject macCall = expression.car();
    if (!(macCall instanceof Symbol)) {
      whatToDo.eat(expression);
      return;
    }
    Symbol macroName = (Symbol) macCall;
    ArcObject macro = namespace.lookup(macroName.name());
    if (macroName.name().equals("act-of-god")) {
      System.out.println("looking up macro for " + macroName + "; found " + macro);
      System.out.println(namespace.fullNamespace());
    }
    if (macro == null) {
      whatToDo.eat(expression);
      return;
    }
    Function fn = (Function) Tagged.ifTagged(macro, "mac");
    if (fn == null) {
      whatToDo.eat(expression);
    } else {
      fn.invoke(thread, namespace, new MacExpander(thread, namespace, whatToDo, !args.cdr().car().isNil()), (Pair) expression.cdr());
    }
  }
}
