package rainbow.types;

import rainbow.*;
import rainbow.types.Pair.NotPair;
import rainbow.vm.ArcThread;
import rainbow.vm.Continuation;

import java.util.Collection;

public abstract class ArcObject implements Function {
  public static final Symbol TYPE_DISPATCHER_TABLE = (Symbol) Symbol.make("call*");
  public static final Nil NIL = Nil.NIL;
  public static final Nil EMPTY_LIST = Nil.EMPTY_LIST;
  public static final Truth T = Truth.T;

  public boolean literal() {
    return false;
  }

  public void invoke(ArcThread thread, LexicalClosure lc, Continuation caller, Pair args) {
    ((Hash) TYPE_DISPATCHER_TABLE.value()).value(type()).invoke(thread, lc, caller, new Pair(this, args));
  }

  public void interpret(ArcThread thread, LexicalClosure lc, Continuation caller) {
    caller.receive(this);
  }

  public long len() {
    throw new ArcError("len: expects one string, list or hash argument, got " + this.type());
  }

  public ArcObject scar(ArcObject newCar) {
    throw new ArcError("Can't set car of " + this.type());
  }

  public ArcObject sref(Pair args) {
    throw new ArcError("Can't sref " + this.type());
  }

  public boolean isNil() {
    return false;
  }

  public int compareTo(ArcObject right) {
    return 0;
  }

  public ArcObject car() {
    throw new ArcError("Can't take car of " + this);
  }

  public ArcObject cdr() {
    throw new ArcError("Can't take cdr of " + this);
  }

  public boolean isCar(Symbol s) {
    return false;
  }

  public abstract ArcObject type();

  public ArcObject copy() {
    return this;
  }

  public Object unwrap() {
    return this;
  }

  public boolean isSame(ArcObject other) {
    return this == other;
  }

  public Collection copyTo(Collection c) {
    return c;
  }

  public boolean isNotPair() {
    return true;
  }

  public Function refFn() {
    throw new ArcError("ref: expects string or hash or cons");
  }

  public void mustBePairOrNil() throws NotPair {
    throw new Pair.NotPair();
  }

  public void setSymbolValue(LexicalClosure lc, ArcObject value) {
    throw new ArcError("set: expects symbol, got " + this);
  }

  public void mustBeNil() throws NotNil {
    throw new NotNil();
  }

  public static class NotNil extends Throwable {
  }
}
