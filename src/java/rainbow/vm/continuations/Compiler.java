package rainbow.vm.continuations;

import rainbow.vm.ArcThread;
import rainbow.vm.Continuation;
import rainbow.Bindings;
import rainbow.Function;
import rainbow.functions.Evaluation;
import rainbow.types.Pair;
import rainbow.types.ArcObject;
import rainbow.types.Symbol;
import rainbow.types.Tagged;

public class Compiler extends ContinuationSupport {
  private Pair expression;

  public Compiler(ArcThread thread, Bindings namespace, Continuation whatToDo, Pair expression) {
    super(thread, namespace, whatToDo);
    this.expression = expression;
  }

  public static void compile(ArcThread thread, Bindings namespace, Continuation whatToDo, ArcObject expression) {
    if (expression.isNil()) {
      whatToDo.eat(expression);
    } else if (Evaluation.isSpecialSyntax(expression)) {
      ArcObject ssExpanded = Evaluation.ssExpand(expression);
      ssExpanded.sourceFrom(expression);
      compile(thread, namespace, whatToDo, ssExpanded);
    } else if (expression instanceof Pair) {
      new Compiler(thread, namespace, whatToDo, (Pair) expression).start();
    } else {
      whatToDo.eat(expression);
    }
  }

  public void start() {
    Function f = getMacro(expression);
    if (f != null) {
      f.invoke(thread, namespace, this, (Pair) expression.cdr());
    } else {
      ArcObject fun = expression.car();
      if (Symbol.is("quote", fun)) {
        whatToDo.eat(expression);
      } else if (Symbol.is("quasiquote", fun)) { //
        Rebuilder rebuilder = new Rebuilder(thread, namespace, whatToDo, QuasiQuoteCompiler.QUASIQUOTE);
        QuasiQuoteCompiler.compile(thread, namespace, rebuilder, expression.cdr().car());
      } else if (Symbol.is("fn", fun)) {
        new FunctionBodyBuilder(thread, namespace, whatToDo, (Pair) expression.cdr()).start();
      } else {
        new PairExpander(thread, namespace, new MacExpander(thread, namespace, this, false), expression).start();
      }
    }
  }

  private Function getMacro(Pair maybeMacCall) {
    ArcObject first = maybeMacCall.car();
    if (!(first instanceof Symbol)) {
      return null;
    }

    Symbol sym = (Symbol) first;
    ArcObject maybeTagged = namespace.lookup(sym.name());
    if (maybeTagged == null) {
      return null;
    }

    return (Function) Tagged.ifTagged(maybeTagged, "mac");
  }

  protected void digest(ArcObject returned) {
    if (expression.equals(returned)) {
      whatToDo.eat(expression);
    } else {
      compile(thread, namespace, whatToDo, returned);
    }
  }
}
