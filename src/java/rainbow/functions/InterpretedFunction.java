package rainbow.functions;

import rainbow.ArcError;
import rainbow.Function;
import rainbow.LexicalClosure;
import rainbow.types.ArcObject;
import rainbow.types.Pair;
import rainbow.types.Symbol;
import rainbow.vm.ArcThread;
import rainbow.vm.Continuation;
import rainbow.vm.continuations.FunctionEvaluator;
import rainbow.vm.continuations.NamespaceBuilder;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class InterpretedFunction extends ArcObject implements Function {
  private ArcObject parameterList;
  private Map lexicalBindings;
  ArcObject[] body;
  boolean complexParams;

  public InterpretedFunction(ArcObject parameterList, Map lexicalBindings, Pair body, ArcObject complexParams) {
    this.parameterList = parameterList;
    this.lexicalBindings = lexicalBindings;
    this.body = body.toArray();
    this.complexParams = !complexParams.isNil();
  }

  public void interpret(ArcThread thread, LexicalClosure lc, Continuation caller) {
    caller.receive(new Threads.Closure(this, lc));
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
    if (parameterList.isNil()) {
      FunctionEvaluator.evaluate(thread, lc, caller, this);
    } else if (complexParams) {
      new NamespaceBuilder(thread, new LexicalClosure(lexicalBindings.size(), lc), caller, parameterList, args, this);
    } else {
      lc = new LexicalClosure(lexicalBindings.size(), lc);
      NamespaceBuilder.simple(lc, parameterList, args);
      FunctionEvaluator.evaluate(thread, lc, caller, this);
    }
  }

  public ArcObject nth(int index) {
    return body[index];
  }

  public boolean last(int index) {
    return index >= body.length;
  }

  public int length() {
    return body.length;
  }
}
