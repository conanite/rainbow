package rainbow.vm.continuations;

import rainbow.LexicalClosure;
import rainbow.functions.Builtin;
import rainbow.types.ArcObject;
import rainbow.types.Pair;
import rainbow.types.Symbol;
import rainbow.vm.ArcThread;
import rainbow.vm.Continuation;

import java.util.LinkedList;
import java.util.Map;

public class QuasiQuoteCompiler extends ContinuationSupport {
  public static final Symbol QUASIQUOTE = (Symbol) Symbol.make("quasiquote");
  public static final Symbol UNQUOTE = (Symbol) Symbol.make("unquote");
  public static final Symbol UNQUOTE_SPLICING = (Symbol) Symbol.make("unquote-splicing");

  private Pair expression;
  private Map[] lexicalBindings;
  private LinkedList result = new LinkedList();
  private Symbol rebuildSymbol;

  public QuasiQuoteCompiler(ArcThread thread, LexicalClosure lc, Continuation caller, Pair expression, Map[] lexicalBindings) {
    super(thread, lc, caller);
    this.expression = expression;
    this.lexicalBindings = lexicalBindings;
  }

  public static void compile(ArcThread thread, LexicalClosure lc, Continuation caller, ArcObject expression, Map[] lexicalBindings) {
    if (expression.isNil() || !(expression instanceof Pair)) {
      caller.receive(expression);
    } else {
      if (QuasiQuoteContinuation.isUnQuote(expression)) {
        Rebuilder rebuilder = new Rebuilder(caller, UNQUOTE);
        Compiler.compile(thread, lc, rebuilder, expression.cdr().car(), lexicalBindings);
      } else if (QuasiQuoteContinuation.isUnQuoteSplicing(expression)) {
        QuasiQuoteCompiler qqc = new QuasiQuoteCompiler(thread, lc, caller, ArcObject.NIL, lexicalBindings);
        qqc.rebuildSymbol = UNQUOTE_SPLICING;
        Compiler.compile(qqc.thread, qqc.lc, qqc, expression.cdr().car(), qqc.lexicalBindings);
      } else if (QuasiQuoteContinuation.isQuasiQuote(expression)) {
        caller.receive(expression);
      } else {
        new QuasiQuoteCompiler(thread, lc, caller, (Pair) expression, lexicalBindings).start();
      }
    }
  }

  private void start() {
    if (expression.isNil()) {
      caller.receive(Pair.buildFrom(result));
      return;
    }

    ArcObject next = expression.car();
    expression = Pair.cast(expression.cdr(), this);
    if (next.isNil() || !(next instanceof Pair)) {
      continueWith(next);
    } else {
      if (QuasiQuoteContinuation.isUnQuote(next)) {
        rebuildSymbol = UNQUOTE;
        Compiler.compile(thread, lc, this, next.cdr().car(), lexicalBindings);
      } else if (QuasiQuoteContinuation.isUnQuoteSplicing(next)) {
        rebuildSymbol = UNQUOTE_SPLICING;
        Compiler.compile(thread, lc, this, next.cdr().car(), lexicalBindings);
      } else if (QuasiQuoteContinuation.isQuasiQuote(next)) {
        continueWith(next);
      } else {
        rebuildSymbol = null;
        new QuasiQuoteCompiler(thread, lc, this, (Pair) next, lexicalBindings).start();
      }
    }
  }

  private void continueWith(ArcObject next) {
    result.add(next);
    start();
  }

  protected void onReceive(ArcObject returned) {
    if (rebuildSymbol != null) {
      continueWith(Pair.buildFrom(rebuildSymbol, returned));
    } else {
      continueWith(returned);
    }
  }

  public Continuation cloneFor(ArcThread thread) {
    QuasiQuoteCompiler qqc = (QuasiQuoteCompiler) super.cloneFor(thread);
    qqc.expression = expression.copy();
    qqc.result = new LinkedList(result);
    return qqc;
  }
}
