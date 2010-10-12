package rainbow.functions.interpreted;

import rainbow.ArcError;
import rainbow.Console;
import rainbow.LexicalClosure;
import rainbow.Nil;
import rainbow.functions.Builtin;
import rainbow.types.*;
import rainbow.vm.Instruction;
import rainbow.vm.VM;
import rainbow.vm.compiler.FunctionBodyBuilder;
import rainbow.vm.compiler.FunctionParameterListBuilder;
import rainbow.vm.instructions.Close;
import rainbow.vm.instructions.Literal;
import rainbow.vm.instructions.PopArg;
import rainbow.vm.interpreter.*;
import rainbow.vm.interpreter.visitor.FunctionOwnershipVisitor;
import rainbow.vm.interpreter.visitor.MeasureLexicalReach;
import rainbow.vm.interpreter.visitor.ReferenceCounter;
import rainbow.vm.interpreter.visitor.Visitor;

import java.util.*;

public abstract class InterpretedFunction extends ArcObject implements Cloneable {
  protected ArcObject name = NIL;
  protected ArcObject parameterList;
  public final Map<Symbol, Integer> lexicalBindings;
  public ArcObject[] body;
  protected Pair instructions;
  private InterpretedFunction lexicalOwner;
  protected InterpretedFunction curried;

  protected InterpretedFunction(ArcObject parameterList, Map lexicalBindings, Pair body) {
    this.parameterList = parameterList;
    this.lexicalBindings = lexicalBindings;
    this.body = body.toArray();
    buildInstructionList();
  }

  public void unassigned(ArcObject name) {
    if (this.name == name) {
      this.name = NIL;
    }
  }

  public void assigned(ArcObject name) {
    this.name = name;

    Visitor v = new FunctionOwnershipVisitor(this);
    for (ArcObject o : body) {
      o.visit(v);
    }
  }

  public ArcObject assignedName() {
    return name;
  }

  private String profileName;

  public String profileName() {
    if (profileName != null) {
      return profileName;
    }

    if (name instanceof Nil) {
      profileName = this.toString();
      if (lexicalOwner != null) {
        profileName += " in " + lexicalOwner.profileName();
      }
    } else {
      profileName = assignedName().toString();
    }

    return profileName;
  }

  private String localProfileName;

  public String localProfileName() {
    if (localProfileName != null) {
      return localProfileName;
    }

    if (name instanceof Nil) {
      localProfileName = this.toString();
    } else {
      localProfileName = assignedName().toString();
    }

    return localProfileName;
  }

  public Pair ownerHierarchy() {
    ArcString me = ArcString.make(localProfileName());
    if (lexicalOwner != null) {
      return new Pair(me, lexicalOwner.ownerHierarchy());
    } else {
      return new Pair(me, NIL);
    }
  }

  public int lexicalDepth() {
    if (lexicalOwner == null) {
      return 1;
    } else {
      return 1 + lexicalOwner.lexicalDepth();
    }
  }

  public ArcObject reduce() {
    if (Console.stackfunctions && canIGoOnTheStack() && !(parameterList instanceof Nil)) {
      return FunctionBodyBuilder.convertToStackParams(this); // todo: .reduce() this to break out of the if clause in curried optional-arg functions
    } else {
      return this;
    }
  }

  private boolean canIGoOnTheStack() {
    // if I need to make closures, I can't keep my params on the param stack
    // todo this could be more subtle: create LC for params that need it, put all other params on stack. probably poor for performance though
    return !hasClosures();
  }

  private void buildInstructionList() {
    List i = new ArrayList();
    buildInstructions(i);
    instructions = Pair.buildFrom(i);
    claimInstructions(this);
  }

  public void belongsTo(final InterpretedFunction container) {
    lexicalOwner = container;
  }

  public InterpretedFunction lexicalOwner() {
    return lexicalOwner;
  }

  private void claimInstructions(final InterpretedFunction owner) {
    instructions.visit(new Visitor() {
      public void accept(Instruction o) {
        o.belongsTo(owner);
      }
    });
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
    return  body.length == 1
            && inlineableArg(param, arg)
            && !assigns(0)
            && !hasClosures();
  }

  private boolean inlineableArg(Symbol param, ArcObject arg) {
    Integer paramIndex = lexicalBindings.get(param);
    BoundSymbol p = BoundSymbol.make(param, 0, paramIndex);
    return (arg.literal())
            || (arg instanceof Quotation)
            || (arg instanceof Symbol)
            || (arg instanceof BoundSymbol)
            || (countReferences(p) <= 1); // todo this last condition allows inlining of invocations, it's dubious and may cause out-of-order evaluation!
  }

  private int countReferences(BoundSymbol p) {
    ReferenceCounter v = new ReferenceCounter(p);
    for (ArcObject o : body) {
      o.visit(v);
    }
    return v.count();
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

  public boolean nests() {
    return !(parameterList instanceof Nil);
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

  public void invokef(VM vm, ArcObject arg1, ArcObject arg2, ArcObject arg3) {
    invokeN(vm, null, arg1, arg2, arg3);
  }

  public void invokeN(VM vm, LexicalClosure lc, ArcObject arg1, ArcObject arg2, ArcObject arg3) {
    invoke(vm, lc, new Pair(arg1, new Pair(arg2, new Pair(arg3, NIL))));
  }

  public void invoke(VM vm, Pair args) {
    invoke(vm, null, args);
  }

  public void invoke(VM vm, LexicalClosure lc, Pair args) {
    throw new ArcError("error: invoke(vm, lc, args) not implemented in " + this.getClass() + "; " + this);
  }

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

  public boolean literal() {
    return !requiresClosure();
  }

  public boolean requiresClosure() { // todo should ignore (do ...) forms because they get inlined anyway
    // todo but must make sure (fn () ...) is counted as requiring closure if it's an arg in an invocation
    // in other words, (fn () ...) doesn't require a closure if it's in fn position of an invocation
    MeasureLexicalReach v = new MeasureLexicalReach();
    visit(v);
    return v.reach() > -1;
  }

  public void visit(Visitor v) {
    v.accept(this);
    FunctionBodyBuilder.visit(v, parameterList, false);
    for (ArcObject o : body) {
      o.visit(v);
    }
    v.end(this);
    if (curried != null) {
      curried.visit(v);
    }
  }

  public boolean isIdFn() {
    if (parameterList.len() == 1) {
      if (parameterList.car() instanceof Symbol) {
        if (body.length == 1) {
          if (body[0] instanceof BoundSymbol) {
            Symbol p1 = (Symbol) parameterList.car();
            BoundSymbol rv = (BoundSymbol) body[0];
            BoundSymbol equiv = BoundSymbol.make(p1, 0, 0);
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
    if (isBracketFn()) {
      if (body[0] instanceof Nil) {
        return "[]";
      } else {
        String s = body[0].toString();
        return "[" + s.substring(1, s.length() - 1) + "]";
      }
    }
    List<ArcObject> fn = new LinkedList<ArcObject>();
    fn.add(Symbol.mkSym("fn"));
    fn.add(parameterList());
    fn.addAll(Arrays.asList(body));
    return Pair.buildFrom(fn, NIL).toString();
  }

  private boolean isBracketFn() {
    return parameterList instanceof Pair && parameterList.car() == Symbol.mkSym("_") && parameterList.cdr() instanceof Nil && body.length == 1 && !(body[0] instanceof LiteralObject);
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
    BoundSymbol p = BoundSymbol.make(param, 0, paramIndex);
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

  public ArcObject inline(StackSymbol p, ArcObject arg, int paramIndex) {
    if (!(parameterList instanceof Nil)) {
      return this;
    }
    InterpretedFunction fn = cloneThis();

    List newBody = new ArrayList();
    for (int i = 0; i < body.length; i++) {
      newBody.add(body[i].inline(p, arg, paramIndex).reduce());
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

  protected void checkArgsLength(int expected, ArcObject args) {
    if (!args.hasLen(expected)) {
      throw new ArcError("error: " + this + " expects " + expected + " args, got " + args);
    }
  }

  public boolean isAifBody() {
    return body.length == 1 && (body[0] instanceof IfClause) && ((IfClause)body[0]).isAifIf();
  }
}
