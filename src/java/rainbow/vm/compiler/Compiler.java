package rainbow.vm.compiler;

import rainbow.Function;
import rainbow.LexicalClosure;
import rainbow.functions.Evaluation;
import rainbow.types.*;
import rainbow.vm.Continuation;
import rainbow.vm.continuations.ContinuationSupport;
import rainbow.vm.interpreter.BoundSymbol;
import rainbow.vm.interpreter.Invocation;
import rainbow.vm.interpreter.Quotation;

import java.util.Map;

public class Compiler extends ContinuationSupport {
  private Pair expression;
  private Map[] lexicalBindings;

  public Compiler(LexicalClosure lc, Continuation caller, Pair expression, Map[] lexicalBindings) {
    super(lc, caller);
    this.expression = expression;
    this.lexicalBindings = lexicalBindings;
    start();
  }

  public static void compile(LexicalClosure lc, Continuation caller, ArcObject expression, Map[] lexicalBindings) {
    if (expression.isNil()) {
      caller.receive(expression);
    } else if (Evaluation.isSpecialSyntax(expression)) {
      compile(lc, caller, Evaluation.ssExpand(expression), lexicalBindings);
    } else if (expression instanceof Pair) {
      new Compiler(lc, caller, (Pair) expression, lexicalBindings);
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
      f.invoke(lc, this, (Pair) expression.cdr());
    } else {
      ArcObject fun = expression.car();
      if (Symbol.is("quote", fun)) {
        caller.receive(new Quotation(expression.cdr().car()));
      } else if (fun == QuasiQuoteCompiler.QUASIQUOTE) {
        QuasiQuoteBuilder qqb = new QuasiQuoteBuilder(caller);
        QuasiQuoteCompiler.compile(lc, qqb, expression.cdr().car(), lexicalBindings);
      } else if (Symbol.is("fn", fun)) {
        new FunctionBodyBuilder(caller, (Pair) expression.cdr(), lexicalBindings).start();
      } else if (Symbol.is("if", fun)) {
        new IfBuilder(caller, expression.cdr(), lexicalBindings);
      } else if (Symbol.is("assign", fun)) {
        new AssignmentBuilder(caller, expression.cdr(), lexicalBindings);
      } else if (Symbol.is("compose", fun.xcar())) {
        caller.receive(decompose((Pair) fun.cdr(), (Pair) expression.cdr()));
      } else if (Symbol.is("complement", fun.xcar())) {
        caller.receive(decomplement(fun.cdr().car(), (Pair) expression.cdr()));
      } else {
        new PairExpander(new MacExpander(this, false), expression, lexicalBindings).start();
      }
    }
  }

  private static ArcObject decompose(Pair fns, Pair args) {
    return new Pair(fns.car(), fns.cdr().isNil() ? args : new Pair(decompose((Pair) fns.cdr(), args), ArcObject.NIL));
  }

  private static ArcObject decomplement(ArcObject not, Pair args) {
    return new Pair(Symbol.make("no"), new Pair(new Pair(not, args), ArcObject.NIL));
  }

  public static void main(String[] args) {
    ArcObject o = ArcObject.NIL;
    System.out.println(decomplement(Pair.buildFrom(Symbol.make("foo"), Symbol.make("it")), Pair.buildFrom(ArcString.make("bar"))));
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
      compile(lc, caller, returned, lexicalBindings);
    }
  }
}
