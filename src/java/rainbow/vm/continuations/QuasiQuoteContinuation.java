package rainbow.vm.continuations;

import rainbow.Bindings;
import rainbow.ArcError;
import rainbow.types.ArcObject;
import rainbow.types.Pair;
import rainbow.types.Symbol;
import rainbow.vm.ArcThread;
import rainbow.vm.Continuation;
import rainbow.vm.Interpreter;

import java.util.List;
import java.util.LinkedList;

public class QuasiQuoteContinuation extends ContinuationSupport {
  private ArcObject expression;
  private ArcObject source;
  private List<ArcObject> result = new LinkedList<ArcObject>();
  private ArcObject current;

  public QuasiQuoteContinuation(ArcThread thread, Bindings namespace, Continuation whatToDo, ArcObject expression, ArcObject source) {
    super(thread, namespace, whatToDo);
    this.expression = expression;
    this.source = expression;
  }

  public void start() {
    if (expression.isNil()) {
      whatToDo.eat(expression);
    } else if (isUnQuote(expression)) {
      current = expression;
      Interpreter.interpret(thread, namespace, whatToDo, expression.cdr().car());
    } else if (!isPair(expression)) {
      whatToDo.eat(expression);
    } else {
      repeat();
    }
  }

  private void repeat() {
    if (expression.isNil()) {
      whatToDo.eat(Pair.buildFrom(result, ArcObject.NIL));
    } else if (expression instanceof Pair) {
      current = expression.car();
      expression = expression.cdr();
      if (isUnQuote(current)) {
        Interpreter.interpret(thread, namespace, this, current.cdr().car());
      } else if (isUnQuoteSplicing(current)) {
        Interpreter.interpret(thread, namespace, new UnquoteSplicer(this, result), current.cdr().car());
      } else if (isPair(current)) {
        new QuasiQuoteContinuation(thread, namespace, this, current, source).start();
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

  private static boolean isUnQuote(ArcObject expression) {
    if (!isPair(expression)) return false;
    ArcObject first = expression.car();
    return (first instanceof Symbol && ((Symbol) first).name().equals("unquote"));
  }

  private static boolean isUnQuoteSplicing(ArcObject expression) {
    if (!isPair(expression)) return false;
    ArcObject first = expression.car();
    return (first instanceof Symbol && ((Symbol) first).name().equals("unquote-splicing"));
  }

  private static boolean isPair(ArcObject expression) {
    return expression instanceof Pair;
  }

  public void digest(ArcObject o) {
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
