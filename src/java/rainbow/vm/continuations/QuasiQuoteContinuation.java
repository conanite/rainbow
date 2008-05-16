package rainbow.vm.continuations;

import rainbow.ArcError;
import rainbow.LexicalClosure;
import rainbow.types.ArcObject;
import rainbow.types.Pair;
import rainbow.vm.ArcThread;
import rainbow.vm.Continuation;
import rainbow.vm.Interpreter;

import java.util.List;
import java.util.LinkedList;

public class QuasiQuoteContinuation extends ContinuationSupport {
  private ArcObject expression;
  private List<ArcObject> result = new LinkedList<ArcObject>();
  private ArcObject current;

  public QuasiQuoteContinuation(ArcThread thread, LexicalClosure lc, Continuation whatToDo, ArcObject expression) {
    super(thread, lc, whatToDo);
    this.expression = expression;
  }

  public void start() {
    if (expression.isNil()) {
      caller.receive(expression);
    } else if (isUnQuote(expression)) {
      current = expression;
      Interpreter.interpret(thread, lc, caller, expression.cdr().car());
    } else if (!isPair(expression)) {
      caller.receive(expression);
    } else {
      repeat();
    }
  }

  private void repeat() {
    if (expression.isNil()) {
      caller.receive(Pair.buildFrom(result, ArcObject.NIL));
    } else if (expression instanceof Pair) {
      current = expression.car();
      expression = expression.cdr();
      if (isUnQuote(current)) {
        Interpreter.interpret(thread, lc, this, current.cdr().car());
      } else if (isUnQuoteSplicing(current)) {
        Interpreter.interpret(thread, lc, new UnquoteSplicer(this, result), current.cdr().car());
      } else if (isQuasiQuote(current)) {
        append(current);
        repeat();
      } else if (isPair(current)) {
        new QuasiQuoteContinuation(thread, lc, this, current).start();
      } else {
        append(current);
        repeat();
      }
    } else {
      throw new ArcError("quasiquote: expected pair, got " + expression);
    }
  }

  private void append(ArcObject first) {
    result.add(first);
  }

  public static boolean isUnQuote(ArcObject expression) {
    return expression.isCar(QuasiQuoteCompiler.UNQUOTE);
  }

  public static boolean isUnQuoteSplicing(ArcObject expression) {
    return expression.isCar(QuasiQuoteCompiler.UNQUOTE_SPLICING);
  }

  public static boolean isQuasiQuote(ArcObject expression) {
    return expression.isCar(QuasiQuoteCompiler.QUASIQUOTE);
  }

  private static boolean isPair(ArcObject expression) {
    return expression instanceof Pair;
  }

  public void onReceive(ArcObject o) {
    if (o != null) {
      append(o);
    }
    repeat();
  }

  protected ArcObject getCurrentTarget() {
    return current;
  }

  public Continuation cloneFor(ArcThread thread) {
    QuasiQuoteContinuation e = (QuasiQuoteContinuation) super.cloneFor(thread);
    e.expression = this.expression.copy();
    e.result = new LinkedList(this.result);
    return e;
  }
}
