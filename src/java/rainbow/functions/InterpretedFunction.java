package rainbow.functions;

import rainbow.ArcError;
import rainbow.LexicalClosure;
import rainbow.types.ArcObject;
import rainbow.types.Pair;
import rainbow.types.Symbol;
import rainbow.vm.VM;
import rainbow.vm.continuations.NamespaceBuilder;
import rainbow.vm.instructions.Close;
import rainbow.vm.instructions.Literal;
import rainbow.vm.instructions.PopArg;

import java.util.*;

public abstract class InterpretedFunction extends ArcObject {
  protected Map lexicalBindings;
  ArcObject[] body;
  protected final Pair instructions;

  public InterpretedFunction(Map lexicalBindings, Pair body) {
    this.lexicalBindings = lexicalBindings;
    this.body = body.toArray();
    List i = new ArrayList();
    if (this.body.length > 0) {
      Pair b = body;
      while (!b.isNil()) {
        b.car().addInstructions(i);
        b = (Pair) b.cdr();
        if (!b.isNil()) {
          i.add(new PopArg("intermediate-fn-expression"));
        }
      }
    } else {
      i.add(new Literal(NIL));
    }
    instructions = Pair.buildFrom(i);
  }

  public abstract void invoke(VM vm, LexicalClosure lc, Pair args);

  public Pair instructions() {
    return instructions;
  }

  public void addInstructions(List i) {
    i.add(new Close(this));
  }

  public int compareTo(ArcObject right) {
    throw new ArcError("Can't compare " + this + " to " + right);
  }

  public String toString() {
    List<ArcObject> fn = new LinkedList<ArcObject>();
    fn.add(Symbol.mkSym("fn"));
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

  public ArcObject last() {
    return body[body.length - 1];
  }

  public int length() {
    return body.length;
  }

  public static class ZeroArgs extends InterpretedFunction {
    public ZeroArgs(Map lexicalBindings, Pair body) {
      super(lexicalBindings, body);
    }

    public void invoke(VM vm, LexicalClosure lc, Pair args) {
      vm.pushFrame(lc, this.instructions);
    }
  }

  public static class ComplexArgs extends InterpretedFunction {
    private ArcObject parameterList;

    public ComplexArgs(ArcObject parameterList, Map lexicalBindings, Pair body) {
      super(lexicalBindings, body);
      this.parameterList = parameterList;
    }

    public void invoke(VM vm, LexicalClosure lc, Pair args) {
      lc = new LexicalClosure(lexicalBindings.size(), lc);
      NamespaceBuilder.complex(vm, lc, parameterList, args);
      vm.pushFrame(lc, this.instructions);
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

    public void invoke(VM vm, LexicalClosure lc, Pair args) {
      lc = new LexicalClosure(lexicalBindings.size(), lc);
      NamespaceBuilder.simple(lc, parameterList, args);
      vm.pushFrame(lc, this.instructions);
    }

    public ArcObject parameterList() {
      return parameterList;
    }
  }
}
