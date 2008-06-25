package rainbow.vm.compiler;

import rainbow.LexicalClosure;
import rainbow.types.ArcObject;
import rainbow.types.Pair;
import rainbow.types.Symbol;
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

  public PairExpander(ArcThread thread, LexicalClosure lc, Continuation caller, Pair expressions, Map[] lexicalBindings) {
    super(thread, lc, caller);
    this.body = expressions;
    this.lexicalBindings = lexicalBindings;
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
    Compiler.compile(thread, lc, this, next, lexicalBindings);
  }

  protected void onReceive(ArcObject returned) {
    if (atLast) {
      Pair expandedBody = Pair.buildFrom(result, returned);
      caller.receive(expandedBody);
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
