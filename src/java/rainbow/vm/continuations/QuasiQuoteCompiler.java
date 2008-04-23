package rainbow.vm.continuations;

import rainbow.vm.ArcThread;
import rainbow.vm.Continuation;
import rainbow.vm.Interpreter;
import rainbow.Bindings;
import rainbow.functions.Builtin;
import rainbow.types.ArcObject;
import rainbow.types.Pair;
import rainbow.types.Symbol;

import java.util.LinkedList;

public class QuasiQuoteCompiler extends ContinuationSupport {
  public static final Symbol QUASIQUOTE = (Symbol) Symbol.make("quasiquote");
  public static final Symbol UNQUOTE = (Symbol) Symbol.make("unquote");
  public static final Symbol UNQUOTE_SPLICING = (Symbol) Symbol.make("unquote-splicing");

  private Pair expression;
  private LinkedList result = new LinkedList();
  private Symbol rebuildSymbol;

  public QuasiQuoteCompiler(ArcThread thread, Bindings namespace, Continuation whatToDo, Pair expression) {
    super(thread, namespace, whatToDo);
    this.expression = expression;
  }

  public static void compile(ArcThread thread, Bindings namespace, Continuation whatToDo, ArcObject expression) {
    if (expression.isNil() || !(expression instanceof Pair)) {
      whatToDo.eat(expression);
    } else {
      new QuasiQuoteCompiler(thread, namespace, whatToDo, (Pair) expression).start();
    }
  }

  private void start() {
    if (expression.isNil()) {
      whatToDo.eat(Pair.buildFrom(result));
      return;
    }

    ArcObject next = expression.car();
    expression = Builtin.cast(expression.cdr(), Pair.class);
    if (next.isNil()) {
      continueWith(next);
    } else if (next instanceof Pair) {
      if (QuasiQuoteContinuation.isUnQuote(next)) {
        this.rebuildSymbol = UNQUOTE;
        Compiler.compile(thread, namespace, this, next.cdr().car());
      } else if (QuasiQuoteContinuation.isUnQuoteSplicing(next)) {
        this.rebuildSymbol = UNQUOTE_SPLICING;
        Compiler.compile(thread, namespace, this, next.cdr().car());
      } else if (QuasiQuoteContinuation.isQuasiQuote(next)) {
        continueWith(next);
      } else {
        this.rebuildSymbol = null;
        new QuasiQuoteCompiler(thread, namespace, this, (Pair) next).start();
      }
    } else {
      continueWith(next);
    }
  }

  private void continueWith(ArcObject next) {
    result.add(next);
    start();
  }

  protected void digest(ArcObject returned) {
    if (rebuildSymbol != null) {
      continueWith(Pair.buildFrom(rebuildSymbol, returned));
    } else {
      continueWith(returned);
    }
  }

  public Continuation cloneFor(ArcThread thread) {
    QuasiQuoteCompiler qqc = (QuasiQuoteCompiler) super.cloneFor(thread);
    qqc.expression = expression.copy();
    result = new LinkedList(result);
    return qqc;
  }
}
