package rainbow.functions.interpreted;

import rainbow.ArcError;
import rainbow.LexicalClosure;
import rainbow.Nil;
import rainbow.functions.Builtin;
import rainbow.types.ArcObject;
import rainbow.types.Pair;
import rainbow.types.Symbol;
import rainbow.vm.VM;
import rainbow.vm.compiler.FunctionBodyBuilder;
import rainbow.vm.compiler.FunctionParameterListBuilder;
import rainbow.vm.instructions.Close;
import rainbow.vm.instructions.Literal;
import rainbow.vm.instructions.PopArg;
import rainbow.vm.interpreter.BoundSymbol;

import java.util.*;

public abstract class InterpretedFunction extends ArcObject implements Cloneable {
  protected ArcObject parameterList;
  protected Map lexicalBindings;
  protected ArcObject[] body;
  protected Pair instructions;

  protected InterpretedFunction(ArcObject parameterList, Map lexicalBindings, Pair body) {
    this.parameterList = parameterList;
    this.lexicalBindings = lexicalBindings;
    this.body = body.toArray();
    buildInstructionList(body);
  }

  private void buildInstructionList(Pair body) {
    List i = new ArrayList();
    if (this.body.length > 0) {
      Pair b = body;
      while (!(b instanceof Nil)) {
        b.car().addInstructions(i);
        b = (Pair) b.cdr();
        if (!(b instanceof Nil)) {
          i.add(new PopArg("intermediate-fn-expression"));
        }
      }
    } else {
      i.add(new Literal(NIL));
    }
    instructions = Pair.buildFrom(i);
  }

  public boolean canInline() {
    if (((parameterList instanceof Pair)) && (parameterList.cdr() instanceof Nil) && ((parameterList.car() instanceof Symbol)) && (body.length == 1)) {
      BoundSymbol p = new BoundSymbol((Symbol) parameterList.car(), 0, 0);
      return !assigned(p) && !hasClosures();// && countReferences(p) <= 1;
    }
    return false;
  }

  public boolean hasClosures() {
    for (ArcObject o : body) {
      if (o instanceof InterpretedFunction) {
        if (((InterpretedFunction)o).requiresClosure()) {
          return true;
        }
      } else if (o.hasClosures()) {
        return true;
      }
    }
    return false;
  }

  private boolean assigned(BoundSymbol p) {
    for (ArcObject o : body) {
      if (o.assigns(p)) {
        return true;
      }
    }
    return false;
  }

  public boolean assigns(BoundSymbol p) {
    if (!(parameterList instanceof Nil)) {
      p = p.nest();
    }
    return assigned(p);
  }

  public void invokef(VM vm) {
    invokeN(vm, null);
  }

  public void invokeN(VM vm, LexicalClosure lc) {
    invoke(vm, lc, NIL);
  }

  public void invokef(VM vm, ArcObject arg) {
    invokeN(vm, null, arg);
  }

  public void invokeN(VM vm, LexicalClosure lc, ArcObject arg) {
    invoke(vm, lc, new Pair(arg, NIL));
  }

  public void invokef(VM vm, ArcObject arg1, ArcObject arg2) {
    invokeN(vm, null, arg1, arg2);
  }

  public void invokeN(VM vm, LexicalClosure lc, ArcObject arg1, ArcObject arg2) {
    invoke(vm, lc, new Pair(arg1, new Pair(arg2, NIL)));
  }

  public void invoke(VM vm, Pair args) {
    invoke(vm, null, args);
  }

  public abstract void invoke(VM vm, LexicalClosure lc, Pair args);

  public Pair instructions() {
    return instructions;
  }

  public void addInstructions(List i) {
    if (requiresClosure()) {
      i.add(new Close(this));
    } else {
      i.add(new Literal(this));
    }
  }

  public boolean requiresClosure() {
    return highestLexicalScopeReference() > -1;
  }

  public int highestLexicalScopeReference() {
    int highest = Integer.MIN_VALUE;
    for (ArcObject expr : body) {
      int eh = expr.highestLexicalScopeReference();
      if (eh > highest) {
        highest = eh;
      }
    }

    highest = FunctionBodyBuilder.highestLexScopeReference(highest, parameterList, false);

    if (parameterList() instanceof Nil) {
      return highest;
    } else {
      return highest - 1;
    }
  }

  public boolean isIdFn() {
    if (parameterList.len() == 1) {
      if (parameterList.car() instanceof Symbol) {
        if (body.length == 1) {
          if (body[0] instanceof BoundSymbol) {
            Symbol p1 = (Symbol) parameterList.car();
            BoundSymbol rv = (BoundSymbol) body[0];
            BoundSymbol equiv = new BoundSymbol(p1, 0, 0);
            return rv.isSameBoundSymbol(equiv);
          }
        }

      }
    }
    return false;
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
    return parameterList;
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

  protected void requireNil(ArcObject test, ArcObject info) {
    try {
      test.cdr().mustBeNil();
    } catch (NotNil notNil) {
      throwArgMismatchError(info);
    }
  }

  protected void requireNotNil(Pair destructured, ArcObject arg) {
    if (destructured instanceof Nil) {
      throwArgMismatchError(arg);
    }
  }

  protected void throwArgMismatchError(ArcObject args) {
    throw new ArcError("args " + args + " doesn't match signature for " + this);
  }

  public ArcObject curry(Symbol param, ArcObject arg) {
    ArcObject newParams = FunctionParameterListBuilder.remove(parameterList, param);
    Map lexicalBindings = new HashMap();
    FunctionParameterListBuilder.index(newParams, lexicalBindings, new int[] {0}, false);
    boolean unnest = newParams instanceof Nil;

    BoundSymbol p = new BoundSymbol(param, 0, 0);
    List newBody = new ArrayList();
    for (int i = 0; i < body.length; i++) {
      newBody.add(body[i].inline(p, arg, unnest).reduce());
    }
    Pair nb = Pair.buildFrom(newBody);
    ArcObject complexParams = FunctionParameterListBuilder.isComplex(newParams);
//    System.out.println("curry: replacing " + this + " with (fn " + newParams + " " + newBody + ")");
    return FunctionBodyBuilder.buildFunctionBody(newParams, lexicalBindings, nb, complexParams);
  }

  public ArcObject inline(BoundSymbol p, ArcObject arg, boolean unnest) {
    if (!(parameterList instanceof Nil)) {
      p = p.nest();
    }
    InterpretedFunction fn = cloneThis();

    List newBody = new ArrayList();
    for (int i = 0; i < body.length; i++) {
      newBody.add(body[i].inline(p, arg, false).reduce());
    }
    Pair nb = Pair.buildFrom(newBody);
    fn.body = nb.toArray();
    fn.buildInstructionList(nb);
    return fn;
  }

  private InterpretedFunction cloneThis() {
    try {
      return (InterpretedFunction) clone();
    } catch (CloneNotSupportedException e) {
      throw new ArcError("couldn't clone " + this + "; " + e, e);
    }
  }
}
