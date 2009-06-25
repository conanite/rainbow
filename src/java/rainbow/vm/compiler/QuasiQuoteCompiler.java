package rainbow.vm.compiler;

import rainbow.LexicalClosure;
import rainbow.types.ArcObject;
import rainbow.types.Pair;
import rainbow.types.Symbol;
import rainbow.vm.ArcThread;
import rainbow.vm.Continuation;
import rainbow.vm.continuations.ContinuationSupport;
import rainbow.vm.continuations.QuasiQuoteContinuation;

import java.util.LinkedList;
import java.util.Map;

public class QuasiQuoteCompiler extends ContinuationSupport {
  public static final Symbol QUASIQUOTE = (Symbol) Symbol.make("quasiquote");
  public static final Symbol UNQUOTE = (Symbol) Symbol.make("unquote");
  public static final Symbol UNQUOTE_SPLICING = (Symbol) Symbol.make("unquote-splicing");

  private ArcObject expression;
  private Map[] lexicalBindings;
  private LinkedList result = new LinkedList();
  private Symbol rebuildSymbol;
  private int nesting;

  private QuasiQuoteCompiler(Continuation caller, ArcObject expression, Map[] lexicalBindings) {
    this(caller, expression, lexicalBindings, 1);
  }

  private QuasiQuoteCompiler(Continuation caller, ArcObject expression, Map[] lexicalBindings, int nesting) {
    super(caller);
    this.expression = expression;
    this.lexicalBindings = lexicalBindings;
    this.nesting = nesting;
  }

  public static void compile(LexicalClosure lc, Continuation caller, ArcObject expression, Map[] lexicalBindings) {
    if (expression.isNotPair()) {
      caller.receive(expression);
      return;
    }

    if (QuasiQuoteContinuation.isUnQuote(expression)) {
      Rebuilder rebuilder = new Rebuilder(caller, UNQUOTE);
      Compiler.compile(lc, rebuilder, expression.cdr().car(), lexicalBindings);

    } else if (QuasiQuoteContinuation.isUnQuoteSplicing(expression)) {
      QuasiQuoteCompiler qqc = new QuasiQuoteCompiler(caller, ArcObject.NIL, lexicalBindings);
      qqc.rebuildSymbol = UNQUOTE_SPLICING;
      Compiler.compile(qqc.lc, qqc, expression.cdr().car(), qqc.lexicalBindings);

    } else if (QuasiQuoteContinuation.isQuasiQuote(expression)) {
      new QuasiQuoteCompiler(caller, expression, lexicalBindings, 2).start();

    } else {
      new QuasiQuoteCompiler(caller, expression, lexicalBindings).start();
    }
  }

  private void start() {
    if (expression.isNotPair()) {
      caller.receive(Pair.buildFrom(result, expression));
      return;
    }

    ArcObject next = expression.car();
    expression = expression.cdr();
    if (next.isNotPair()) {
      continueWith(next);
      return;
    }

    if (QuasiQuoteContinuation.isUnQuote(next)) {
      if (nesting == 1) {
        rebuildSymbol = UNQUOTE;
        Compiler.compile(lc, this, next.cdr().car(), lexicalBindings);
      } else {
        new QuasiQuoteCompiler(this, next, lexicalBindings, nesting - 1).start();
      }

    } else if (QuasiQuoteContinuation.isUnQuoteSplicing(next)) {
      if (nesting == 1) {
        rebuildSymbol = UNQUOTE_SPLICING;
        Compiler.compile(lc, this, next.cdr().car(), lexicalBindings);
      } else {
        continueWith(next);
      }

    } else if (QuasiQuoteContinuation.isQuasiQuote(next)) {
      rebuildSymbol = null;
      new QuasiQuoteCompiler(this, next, lexicalBindings, nesting + 1).start();

    } else {
      rebuildSymbol = null;
      new QuasiQuoteCompiler(this, next, lexicalBindings, nesting).start();
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
