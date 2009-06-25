package rainbow.vm.compiler;

import rainbow.types.ArcObject;
import rainbow.types.Pair;
import rainbow.vm.ArcThread;
import rainbow.vm.Continuation;
import rainbow.vm.continuations.ContinuationSupport;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class PairExpander extends ContinuationSupport {
  private ArcObject body;
  private Map[] lexicalBindings;
  private List result = new LinkedList();
  private boolean atLast;

  public PairExpander(Continuation caller, Pair expressions, Map[] lexicalBindings) {
    super(caller);
    this.body = expressions;
    this.lexicalBindings = lexicalBindings;
  }

  public void start() {
    if (!body.isNil() && body instanceof Pair) {
      ArcObject next = body.car();
      body = body.cdr();
      Compiler.compile(lc, this, next, lexicalBindings);
    } else {
      atLast = true;
      Compiler.compile(lc, this, body, lexicalBindings);
    }
  }

  protected void onReceive(ArcObject returned) {
    if (atLast) {
      caller.receive(Pair.buildFrom(result, returned));
    } else {
      result.add(returned);
      start();
    }
  }

  public Continuation cloneFor(ArcThread thread) {
    PairExpander clone = (PairExpander) super.cloneFor(thread);
    clone.body = body.copy();
    clone.result = new LinkedList(result);
    return clone;
  }
}
