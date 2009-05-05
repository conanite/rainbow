package rainbow.vm.compiler;

import rainbow.Function;
import rainbow.LexicalClosure;
import rainbow.functions.Evaluation;
import rainbow.types.ArcObject;
import rainbow.types.Pair;
import rainbow.types.Symbol;
import rainbow.types.Tagged;
import rainbow.vm.ArcThread;
import rainbow.vm.BoundSymbol;
import rainbow.vm.Continuation;
import rainbow.vm.continuations.ContinuationSupport;

import java.util.Map;

public class Compiler extends ContinuationSupport {
  private Pair expression;
  private Map[] lexicalBindings;

  public Compiler(ArcThread thread, LexicalClosure lc, Continuation caller, Pair expression, Map[] lexicalBindings) {
    super(thread, lc, caller);
    this.expression = expression;
    this.lexicalBindings = lexicalBindings;
  }

  public static void compile(ArcThread thread, LexicalClosure lc, Continuation caller, ArcObject expression, Map[] lexicalBindings) {
    if (expression.isNil()) {
      caller.receive(expression);
    } else if (Evaluation.isSpecialSyntax(expression)) {
      compile(thread, lc, caller, Evaluation.ssExpand(expression), lexicalBindings);
    } else if (expression instanceof Pair) {
      new Compiler(thread, lc, caller, (Pair) expression, lexicalBindings).start();
    } else if (expression instanceof Symbol) {
      for (int i = 0; i < lexicalBindings.length; i++) {
        if (lexicalBindings[i].containsKey(expression)) {
          caller.receive(new BoundSymbol((Symbol)expression, i, (Integer)lexicalBindings[i].get(expression)));
          return;
        }
      }
      caller.receive(expression);
    } else {
      caller.receive(expression);
    }
  }

  public void start() {
    Function f = getMacro(expression);
    if (f != null) {
      f.invoke(thread, lc, this, (Pair) expression.cdr());
    } else {
      ArcObject fun = expression.car();
      if (Symbol.is("quote", fun)) {
        caller.receive(expression);
      } else if (fun == QuasiQuoteCompiler.QUASIQUOTE) {
        Rebuilder rebuilder = new Rebuilder(caller, QuasiQuoteCompiler.QUASIQUOTE);
        QuasiQuoteCompiler.compile(thread, lc, rebuilder, expression.cdr().car(), lexicalBindings);
      } else if (Symbol.is("fn", fun)) {
        new FunctionBodyBuilder(thread, lc, caller, (Pair) expression.cdr(), lexicalBindings).start();
      } else {
        new PairExpander(thread, lc, new MacExpander(thread, lc, this, false), expression, lexicalBindings).start();
      }
    }
  }

  private Function getMacro(Pair maybeMacCall) {
    ArcObject first = maybeMacCall.car();
    if (!(first instanceof Symbol)) {
      return null;
    }

    Symbol sym = (Symbol) first;
    ArcObject maybeTagged = thread.environment().lookup(sym);
    if (maybeTagged == null) {
      return null;
    }

    return (Function) Tagged.ifTagged(maybeTagged, "mac");
  }

  protected void onReceive(ArcObject returned) {
    if (expression.equals(returned)) {
      caller.receive(expression);
    } else {
      compile(thread, lc, caller, returned, lexicalBindings);
    }
  }
}
