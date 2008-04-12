package rainbow.vm.continuations;

import rainbow.types.ArcObject;
import rainbow.types.Pair;
import rainbow.vm.ArcThread;
import rainbow.vm.Continuation;
import rainbow.Bindings;

import java.util.List;
import java.util.LinkedList;

public class PairExpander extends ContinuationSupport {
  private ArcObject body;
  private List result = new LinkedList();
  private boolean atLast;

  public PairExpander(ArcThread thread, Bindings namespace, Continuation whatToDo, Pair expressions) {
    super(thread, namespace, whatToDo);
    this.body = expressions;
  }

  public void start() {
    if (body.isNil()) {
      whatToDo.eat(Pair.buildFrom(result));
      return;
    }

    if (body instanceof Pair) {
      ArcObject next = body.car();
      body = body.cdr();
      new ExpressionCompiler(thread, namespace, this, next).start();
    } else {
      atLast = true;
      new ExpressionCompiler(thread, namespace, this, body).start();
    }
  }

  protected void digest(ArcObject returned) {
    if (atLast) {
      Pair expandedBody = Pair.buildFrom(result, returned);
      whatToDo.eat(expandedBody);
    } else {
      result.add(returned);
      start();
    }
  }
}
