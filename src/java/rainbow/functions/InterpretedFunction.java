package rainbow.functions;

import rainbow.*;
import rainbow.vm.ArcThread;
import rainbow.vm.Continuation;
import rainbow.vm.continuations.FunctionEvaluator;
import rainbow.vm.continuations.NamespaceBuilder;
import rainbow.types.ArcObject;
import rainbow.types.Pair;
import rainbow.types.Symbol;

import java.util.List;
import java.util.LinkedList;

public class InterpretedFunction extends ArcObject implements Function {
  private ArcObject parameterList;
  Pair body;
  protected Bindings arc;

  public InterpretedFunction(ArcObject parameterList, Pair body, Bindings arc) {
    this.parameterList = parameterList;
    this.body = body;
    this.arc = arc;
  }

  public int compareTo(ArcObject right) {
    throw new ArcError("Can't compare " + this + " to " + right);
  }

  public String toString() {
    List fn = new LinkedList();
    fn.add(Symbol.make("fn"));
    fn.add(parameterList);
    body.copyTo(fn);
    Pair def = Pair.buildFrom(fn, NIL);
    return def.toString();
  }

  public ArcObject type() {
    return Builtin.TYPE;
  }

  public void invoke(ArcThread thread, Bindings namespace, Continuation whatToDo, Pair args) {
    if (body.isNil()) {
      whatToDo.eat(NIL);
    } else {
      Bindings child = new Bindings(namespace);
      FunctionEvaluator evaluator = new FunctionEvaluator(thread, child, whatToDo, this, body);
      new NamespaceBuilder(thread, child, evaluator, parameterList, args).start();
    }
  }

  public String code() {
    return toString();
  }
}
