package rainbow.vm.continuations;

import rainbow.vm.ArcThread;
import rainbow.vm.Continuation;
import rainbow.Bindings;
import rainbow.Function;
import rainbow.functions.Evaluation;
import rainbow.functions.Macex;
import rainbow.types.Pair;
import rainbow.types.ArcObject;
import rainbow.types.Symbol;
import rainbow.types.Tagged;

public class ExpressionCompiler extends ContinuationSupport {
  private ArcObject expression;

  public ExpressionCompiler(ArcThread thread, Bindings namespace, Continuation whatToDo, ArcObject expression) {
    super(thread, namespace, whatToDo);
    this.expression = expression;
  }

  public void start() {
    if (expression.isNil()) {
      whatToDo.eat(expression);
    } else if (Evaluation.isSpecialSyntax(expression)) {
      expression = Evaluation.ssExpand(expression);
      start();
    } else if (expression instanceof Pair) {
      expand((Pair) expression);
    } else {
      whatToDo.eat(expression);
    }
  }

  private void expand(Pair pair) {
    Function f = getMacro(pair);
    if (f != null) {
      f.invoke(thread, namespace, this, (Pair) pair.cdr());
    } else {
      ArcObject fun = pair.car();
      if (Symbol.is("quote", fun)) {
        whatToDo.eat(pair);
      } else if (Symbol.is("quasiquote", fun)) {
        whatToDo.eat(pair);
      } else if (Symbol.is("fn", fun)) {
        new FunctionBodyBuilder(thread, namespace, whatToDo, (Pair) pair.cdr()).start();
//        new PairExpander(thread, namespace, new MacExpander(thread, namespace, whatToDo, false), pair).start();
      } else {
        new PairExpander(thread, namespace, new MacExpander(thread, namespace, whatToDo, false), pair).start();
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
      expression = returned;
      start();
    }
  }
}
