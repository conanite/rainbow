package rainbow.functions;

import rainbow.ArcError;
import rainbow.Function;
import rainbow.LexicalClosure;
import rainbow.types.ArcObject;
import rainbow.types.Pair;
import rainbow.types.Symbol;
import rainbow.vm.Continuation;
import rainbow.vm.continuations.FunctionEvaluator;
import rainbow.vm.continuations.NamespaceBuilder;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public abstract class InterpretedFunction extends ArcObject implements Function {
  protected Map lexicalBindings;
  ArcObject[] body;

  public InterpretedFunction(Map lexicalBindings, Pair body) {
    this.lexicalBindings = lexicalBindings;
    this.body = body.toArray();
  }

  public void interpret(LexicalClosure lc, Continuation caller) {
    caller.receive(new Threads.Closure(this, lc));
  }

  public int compareTo(ArcObject right) {
    throw new ArcError("Can't compare " + this + " to " + right);
  }

  public String toString() {
    List<ArcObject> fn = new LinkedList<ArcObject>();
    fn.add(Symbol.make("fn"));
    fn.add(parameterList());
    fn.addAll(Arrays.asList(body));
    return Pair.buildFrom(fn, NIL).toString();
  }

  public ArcObject parameterList() {
    return ArcObject.EMPTY_LIST;
  }

  public ArcObject type() {
    return Builtin.TYPE;
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

  public static class ZeroArgs extends InterpretedFunction {
    public ZeroArgs(Map lexicalBindings, Pair body) {
      super(lexicalBindings, body);
    }

    public void invoke(LexicalClosure lc, Continuation caller, Pair args) {
      FunctionEvaluator.evaluate(lc, caller, this);
    }
  }

  public static class ComplexArgs extends InterpretedFunction {
    private ArcObject parameterList;

    public ComplexArgs(ArcObject parameterList, Map lexicalBindings, Pair body) {
      super(lexicalBindings, body);
      this.parameterList = parameterList;
    }

    public void invoke(LexicalClosure lc, Continuation caller, Pair args) {
      new NamespaceBuilder(new LexicalClosure(lexicalBindings.size(), lc), caller, parameterList, args, this);
    }

    public ArcObject parameterList() {
      return parameterList;
    }
  }

  public static class SimpleArgs extends InterpretedFunction {
    private ArcObject parameterList;

    public SimpleArgs(ArcObject parameterList, Map lexicalBindings, Pair body) {
      super(lexicalBindings, body);
      this.parameterList = parameterList;
    }

    public void invoke(LexicalClosure lc, Continuation caller, Pair args) {
      lc = new LexicalClosure(lexicalBindings.size(), lc);
      NamespaceBuilder.simple(lc, parameterList, args);
      FunctionEvaluator.evaluate(lc, caller, this);
    }

    public ArcObject parameterList() {
      return parameterList;
    }
  }
}
