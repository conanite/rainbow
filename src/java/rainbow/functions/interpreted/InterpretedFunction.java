package rainbow.functions.interpreted;

import rainbow.ArcError;
import rainbow.LexicalClosure;
import rainbow.Nil;
import rainbow.functions.Builtin;
import rainbow.types.ArcObject;
import rainbow.types.ArcString;
import rainbow.types.Pair;
import rainbow.types.Symbol;
import rainbow.vm.VM;
import rainbow.vm.instructions.Close;
import rainbow.vm.instructions.Literal;
import rainbow.vm.instructions.PopArg;

import java.util.*;

public abstract class InterpretedFunction extends ArcObject {
  protected final ArcObject parameterList;
  protected final Map lexicalBindings;
  final ArcObject[] body;
  protected final Pair instructions;

  protected InterpretedFunction(ArcObject parameterList, Map lexicalBindings, Pair body) {
    this.parameterList = parameterList;
    this.lexicalBindings = lexicalBindings;
    this.body = body.toArray();
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

  public void invokeN(VM vm, LexicalClosure lc) {
    invoke(vm, lc, NIL);
  }

  public void invokeN(VM vm, LexicalClosure lc, ArcObject arg) {
    invoke(vm, lc, new Pair(arg, NIL));
  }

  public void invokeN(VM vm, LexicalClosure lc, ArcObject arg1, ArcObject arg2) {
    invoke(vm, lc, new Pair(arg1, new Pair(arg2, NIL)));
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

}
