package rainbow.vm.compiler;

import rainbow.Function;
import rainbow.LexicalClosure;
import rainbow.functions.Evaluation;
import rainbow.types.ArcObject;
import rainbow.types.Pair;
import rainbow.types.Symbol;
import rainbow.types.Tagged;
import rainbow.vm.ArcThread;
import rainbow.vm.Continuation;
import rainbow.vm.continuations.ContinuationSupport;
import rainbow.vm.interpreter.BoundSymbol;
import rainbow.vm.interpreter.Invocation;
import rainbow.vm.interpreter.Quotation;

import java.util.Map;

public class Compiler extends ContinuationSupport {
  private Pair expression;
  private Map[] lexicalBindings;

  public Compiler(ArcThread thread, LexicalClosure lc, Continuation caller, Pair expression, Map[] lexicalBindings) {
    super(thread, lc, caller);
    this.expression = expression;
    this.lexicalBindings = lexicalBindings;
    start();
  }

  public static void compile(ArcThread thread, LexicalClosure lc, Continuation caller, ArcObject expression, Map[] lexicalBindings) {
    if (expression.isNil()) {
      caller.receive(expression);
    } else if (Evaluation.isSpecialSyntax(expression)) {
      compile(thread, lc, caller, Evaluation.ssExpand(expression), lexicalBindings);
    } else if (expression instanceof Pair) {
      new Compiler(thread, lc, caller, (Pair) expression, lexicalBindings);
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
        caller.receive(new Quotation(expression.cdr().car()));
      } else if (fun == QuasiQuoteCompiler.QUASIQUOTE) {
        QuasiQuoteBuilder qqb = new QuasiQuoteBuilder(caller);
        QuasiQuoteCompiler.compile(thread, lc, qqb, expression.cdr().car(), lexicalBindings);
      } else if (Symbol.is("fn", fun)) {
        new FunctionBodyBuilder(thread, lc, caller, (Pair) expression.cdr(), lexicalBindings).start();
      } else if (Symbol.is("if", fun)) {
        new IfBuilder(thread, lc, caller, expression.cdr(), lexicalBindings);
      } else if (Symbol.is("assign", fun)) {
        new AssignmentBuilder(thread, lc, caller, expression.cdr(), lexicalBindings);
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
    if (!sym.bound()) {
      return null;
    }

    ArcObject maybeTagged = sym.value();
    return Tagged.ifTagged(maybeTagged, "mac");
  }

  protected void onReceive(ArcObject returned) {
    if (expression.equals(returned)) {
      if (expression.isNotPair()) {
        caller.receive(expression);
      } else {
        Invocation invocation = new Invocation();
        invocation.buildFrom(expression);
        caller.receive(invocation);
      }
    } else {
      compile(thread, lc, caller, returned, lexicalBindings);
    }
  }
}
