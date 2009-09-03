package rainbow.functions.interpreted;

import rainbow.ArcError;
import rainbow.LexicalClosure;
import rainbow.Nil;
import rainbow.functions.Builtin;
import rainbow.types.ArcObject;
import rainbow.types.LiteralObject;
import rainbow.types.Pair;
import rainbow.types.Symbol;
import rainbow.vm.VM;
import rainbow.vm.compiler.FunctionBodyBuilder;
import rainbow.vm.compiler.FunctionParameterListBuilder;
import rainbow.vm.instructions.*;
import rainbow.vm.interpreter.BoundSymbol;
import rainbow.vm.interpreter.Quotation;
import rainbow.vm.interpreter.visitor.Visitor;

import java.util.*;

public abstract class InterpretedFunction extends ArcObject implements Cloneable {
  protected ArcObject parameterList;
  protected Map<Symbol, Integer> lexicalBindings;
  public ArcObject[] body;
  protected Pair instructions;

  protected InterpretedFunction(ArcObject parameterList, Map lexicalBindings, Pair body) {
    this.parameterList = parameterList;
    this.lexicalBindings = lexicalBindings;
    this.body = body.toArray();
    buildInstructionList();
  }

  public ArcObject reduce() {
    canIGoOnTheStack();
    return this;
  }

  private void canIGoOnTheStack() {
    if (hasClosures()) {
      return;
    }

    if (requiresClosure()) {
      return;
    }

    if (lexicalBindings.size() > 1) {
      return;
    }

    if (!(parameterList instanceof Symbol) && !(parameterList.car() instanceof Symbol)) {
      return;
    }

    Symbol p = (Symbol) ((parameterList instanceof Symbol) ? parameterList : parameterList.car());
    BoundSymbol b = new BoundSymbol(p, 0, 0);

    List referrers = findReferrers(b);
    if (referrers.size() > 1) {
      return;
    }

//    System.out.println("referrers to " + b + " in " + this + " : " + referrers);

//    System.out.println("I can go on the stack: " + this);
  }

  private void buildInstructionList() {
    List i = new ArrayList();
    buildInstructions(i);
    instructions = Pair.buildFrom(i);
  }

  public void buildInstructions(List i) {
    if (body.length == 0) {
      i.add(new Literal(NIL));
    } else {
      for (int b = 0; b < body.length; b++) {
        ArcObject expr = body[b];
        boolean last = (b == body.length - 1);
        expr.addInstructions(i);
        if (!last) {
          i.add(new PopArg("intermediate-fn-expression"));
        }
      }
    }
  }

  public boolean canInline(Symbol param, ArcObject arg) {
    Integer paramIndex = lexicalBindings.get(param);
    BoundSymbol p = new BoundSymbol(param, 0, paramIndex);
    return  body.length == 1
            && inlineableArg(p, arg)
            && !assigns(0)
            && !hasClosures();
  }

  private boolean inlineableArg(BoundSymbol p, ArcObject arg) {
    return (arg instanceof LiteralObject)
            || (arg instanceof Quotation)
            || (arg instanceof Symbol)
            || (arg instanceof BoundSymbol)
            || (countReferences(p) <= 1);
  }

  private int countReferences(BoundSymbol p) {
    int refs = 0;
    for (ArcObject o : body) {
      refs = o.countReferences(refs, p);
    }
    return refs;
  }

  public int countReferences(int refs, BoundSymbol p) {
    return refs + countReferences(maybeNest(p));
  }

  private BoundSymbol maybeNest(BoundSymbol p) {
    if (!(parameterList instanceof Nil)) {
      return p.nest(0);
    } else {
      return p;
    }
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

  private boolean assigned(int nesting) {
    for (ArcObject o : body) {
      if (o.assigns(nesting)) {
        return true;
      }
    }
    return false;
  }

  public boolean assigns(int nesting) {
    if (!(parameterList instanceof Nil)) {
      return assigned(nesting + 1);
    } else {
      return assigned(nesting);
    }
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
      highest = FunctionBodyBuilder.highestLexScopeReference(highest, parameterList, false);
    }

    if (parameterList() instanceof Nil) {
      return highest;
    } else {
      return highest - 1;
    }
  }

  public List findReferrers(final BoundSymbol b) {
    final List stack = new ArrayList();
    final List referrers = new ArrayList();

    Visitor v = new Visitor() {
      int fNesting = 0;
      int lNesting = 0;
      BoundSymbol target = b;

      public void accept(InterpretedFunction f) {
        stack.add(0, f);
        fNesting++;
        if (!(f.parameterList instanceof Nil)) {
          target = target.nest(0);
        }
      }

      public void acceptObject(ArcObject o) {
        stack.add(0, o);
      }

      public void endObject(ArcObject o) {
        stack.remove(0);
      }

      public void end(InterpretedFunction f) {
        stack.remove(0);
        fNesting--;
        if (!(f.parameterList instanceof Nil)) {
          target = target.unnest();
        }
      }

      public void accept(BoundSymbol b) {
        if (b.isSameBoundSymbol(target) && stack.size() > 0) {
          referrers.add(stack.get(0));
        }
      }
    };

    for (ArcObject o : body) {
      o.visit(v);
    }

    return referrers;
  }

  public void visit(Visitor v) {
    v.accept(this);
    FunctionBodyBuilder.visit(v, parameterList, false);
    for (ArcObject o : body) {
      o.visit(v);
    }
    v.end(this);
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

  public ArcObject curry(Symbol param, ArcObject arg, boolean requiresNesting) {
    Integer paramIndex = lexicalBindings.get(param);
    BoundSymbol p = new BoundSymbol(param, 0, paramIndex);
    ArcObject newParams = FunctionParameterListBuilder.curry(parameterList, p, arg, paramIndex);
    Map lexicalBindings = new HashMap();
    FunctionParameterListBuilder.index(newParams, lexicalBindings, new int[] {0}, false);
    boolean unnest = newParams instanceof Nil;
    if (!unnest && requiresNesting) {
      arg = arg.nest(0);
    }

    List newBody = new ArrayList();
    for (int i = 0; i < body.length; i++) {
      newBody.add(body[i].inline(p, arg, unnest, 0, paramIndex).reduce());
    }
    Pair nb = Pair.buildFrom(newBody);
    ArcObject complexParams = FunctionParameterListBuilder.isComplex(newParams);
    return FunctionBodyBuilder.buildFunctionBody(newParams, lexicalBindings, nb, complexParams);
  }

  public ArcObject inline(BoundSymbol p, ArcObject arg, boolean unnest, int nesting, int paramIndex) {
    p = maybeNest(p);
    InterpretedFunction fn = cloneThis();

    List newBody = new ArrayList();
    for (int i = 0; i < body.length; i++) {
      newBody.add(body[i].inline(p, arg, false, nesting + 1, paramIndex).reduce());
    }
    Pair nb = Pair.buildFrom(newBody);
    fn.body = nb.toArray();
    fn.buildInstructionList();
    return fn;
  }

  public ArcObject nest(int threshold) {
    if (!(parameterList instanceof Nil)) {
      threshold++;
    }
    InterpretedFunction fn = cloneThis();

    List newBody = new ArrayList();
    for (int i = 0; i < body.length; i++) {
      newBody.add(body[i].nest(threshold));
    }
    Pair nb = Pair.buildFrom(newBody);
    fn.body = nb.toArray();
    fn.buildInstructionList();
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
