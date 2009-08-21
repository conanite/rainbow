package rainbow.types;

import rainbow.*;
import rainbow.types.Pair.NotPair;
import rainbow.vm.VM;
import rainbow.vm.instructions.invoke.Invoke_N;

import java.util.Collection;
import java.util.List;

public abstract class ArcObject {
  public static final Symbol TYPE_DISPATCHER_TABLE = Symbol.mkSym("call*");
  public static final Nil NIL = Nil.NIL;
  public static final Nil EMPTY_LIST = Nil.EMPTY_LIST;
  public static final Truth T = Truth.T;

  public boolean literal() {
    return false;
  }

  public void addInstructions(List i) {
    throw new ArcError("addInstructions not defined on " + this + ", a " + getClass());
  }

  public void invoke(VM vm, Pair args) {
    ((Hash) TYPE_DISPATCHER_TABLE.value()).value(type()).invoke(vm, new Pair(this, args));
  }

  public void invokef(VM vm) {
    invoke(vm, NIL);
  }

  public void invokef(VM vm, ArcObject arg) {
    invoke(vm, new Pair(arg, NIL));
  }

  public void invokef(VM vm, ArcObject arg1, ArcObject arg2) {
    invoke(vm, new Pair(arg1, new Pair(arg2, NIL)));
  }

  public void invokef(VM vm, ArcObject arg1, ArcObject arg2, ArcObject arg3) {
    invoke(vm, new Pair(arg1, new Pair(arg2, new Pair(arg3, NIL))));
  }

  public long len() {
    throw new ArcError("len: expects one string, list or hash argument, got " + this.type());
  }

  public ArcObject scar(ArcObject newCar) {
    throw new ArcError("Can't set car of " + this.type());
  }

  public ArcObject sref(ArcObject value, ArcObject index) {
    throw new ArcError("Can't sref " + this.type() + ", other args were " + value + ", " + index);
  }

  public ArcObject sref(Pair args) {
    throw new ArcError("Can't sref " + this.type());
  }

  public int compareTo(ArcObject right) {
    return 0;
  }

  public ArcObject xcar() {
    return NIL;
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

  public void mustBePairOrNil() throws NotPair {
    throw new Pair.NotPair();
  }

  public void setSymbolValue(LexicalClosure lc, ArcObject value) {
    throw new ArcError("set: expects symbol, got " + this);
  }

  public void mustBeNil() throws NotNil {
    throw new NotNil();
  }

  public ArcObject or(ArcObject other) {
    return this;
  }

  public ArcObject invokeAndWait(VM vm, Pair args) {
    int len = (int) args.len();
    while (!(args instanceof Nil)) {
      vm.pushA(args.car());
      args = (Pair) args.cdr();
    }
    vm.pushA(this);
    vm.pushFrame(new Invoke_N.Other(len));
    return vm.thread();
  }

  public static class NotNil extends Throwable {
  }
}
