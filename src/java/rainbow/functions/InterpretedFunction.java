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
import java.util.Arrays;
import java.util.Map;

public class InterpretedFunction extends ArcObject implements Function {
  private ArcObject parameterList;
  private Map lexicalBindings;
  ArcObject[] body;

  public InterpretedFunction(ArcObject parameterList, Map lexicalBindings, Pair body) {
    this.parameterList = parameterList;
    this.lexicalBindings = lexicalBindings;
    this.body = body.toArray();
  }

  public int compareTo(ArcObject right) {
    throw new ArcError("Can't compare " + this + " to " + right);
  }

  public String toString() {
    List<ArcObject> fn = new LinkedList<ArcObject>();
    fn.add(Symbol.make("fn"));
    fn.add(parameterList);
    fn.addAll(Arrays.asList(body));
    Pair def = Pair.buildFrom(fn, NIL);
    return def.toString();
  }

  public ArcObject type() {
    return Builtin.TYPE;
  }

  public void invoke(ArcThread thread, LexicalClosure lc, Continuation caller, Pair args) {
    if (body.length == 0) {
      caller.receive(NIL);
    } else {
      LexicalClosure childClosure = parameterList.isNil() ? lc : new LexicalClosure(lexicalBindings, lc); // todo this doesn't make any sense: why does childClosure extend the caller's closure?
      FunctionEvaluator evaluator = new FunctionEvaluator(thread, childClosure, caller, this);
      NamespaceBuilder.build(thread, childClosure, evaluator, parameterList, args);
    }
  }

  public String code() {
    return toString();
  }

  public ArcObject nth(int index) {
    return body[index];
  }

  public boolean last(int index) {
    return index >= body.length;
  }
}
