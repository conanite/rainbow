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
  private Pair original;

  public PairExpander(ArcThread thread, Bindings namespace, Continuation whatToDo, Pair expressions) {
    super(thread, namespace, whatToDo);
    this.body = expressions;
    this.original = expressions;
  }

  public void start() {
    if (!body.isNil() && body instanceof Pair) {
      ArcObject next = body.car();
      body = body.cdr();
      compile(next);
    } else {
      atLast = true;
      compile(body);
    }
  }

  private void compile(ArcObject next) {
    Compiler.compile(thread, namespace, this, next);
  }

  protected void digest(ArcObject returned) {
    if (atLast) {
      Pair expandedBody = Pair.buildFrom(result, returned);
      expandedBody.sourceFrom(original);
      whatToDo.eat(expandedBody);
    } else {
      result.add(returned);
      start();
    }
  }
}
