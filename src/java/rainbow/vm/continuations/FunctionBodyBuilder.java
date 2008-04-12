package rainbow.vm.continuations;

import rainbow.Bindings;
import rainbow.functions.InterpretedFunction;
import rainbow.types.ArcObject;
import rainbow.types.Pair;
import rainbow.vm.ArcThread;
import rainbow.vm.Continuation;

import java.util.LinkedList;
import java.util.List;

public class FunctionBodyBuilder extends ContinuationSupport {
  private ArcObject parameters;
  private Pair body;
  private List result = new LinkedList();

  public FunctionBodyBuilder(ArcThread thread, Bindings namespace, Continuation whatToDo, Pair args) {
    super(thread, namespace, whatToDo);
    this.parameters = args.car();
    this.body = ArcObject.cast(args.cdr(), Pair.class);
  }

  public void start() {
    new PairExpander(thread, namespace, this, body).start(); // todo caller should instantiate PairExpander and pass FunctionBodyBuilder as eater
  }

  protected void digest(ArcObject returned) {
    Pair expandedBody = (Pair) returned;
    whatToDo.eat(buildFunctionBody(expandedBody));
  }

  private ArcObject buildFunctionBody(Pair expandedBody) {
    return new InterpretedFunction(parameters, expandedBody, namespace);
  }

  public Continuation cloneFor(ArcThread thread) {
    FunctionBodyBuilder e = (FunctionBodyBuilder) super.cloneFor(thread);
    e.body = this.body.copy();
    e.result = new LinkedList(result);
    return e;
  }
}
