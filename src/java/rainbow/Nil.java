package rainbow;

import rainbow.types.ArcObject;
import rainbow.types.Pair;
import rainbow.types.Symbol;
import rainbow.vm.VM;
import rainbow.vm.instructions.Literal;

import java.util.Collection;
import java.util.List;

public class Nil extends Pair {
  public static final Symbol TYPE = Symbol.TYPE;
  public static final Nil NIL = new Nil("nil");
  public static final Nil EMPTY_LIST = new Nil("()");

  private String rep;

  private Nil(String rep) {
    this.rep = rep;
  }

  public void addInstructions(List i) {
    i.add(new Literal(NIL));
  }

  public void invoke(VM vm, Pair args) {
    throw new ArcError("Function dispatch on inappropriate object: " + this);
  }

  public void mustBePairOrNil() throws NotPair {
  }

  public boolean literal() {
    return true;
  }

  public long len() {
    return 0;
  }

  public boolean isNotPair() {
    return true;
  }

  public void mustBeNil() {
  }

  public String toString() {
    return rep;
  }

  public ArcObject xcar() {
    return this;
  }

  public ArcObject car() {
    return this;
  }

  public ArcObject cdr() {
    return this;
  }

  public boolean isCar(Symbol s) {
    return false;
  }

  public void setCar(ArcObject item) {
    throw new Error("can't set the car of " + this);
  }

  public void setCdr(ArcObject cdr) {
    throw new Error("can't set the cdr of " + this);
  }

  public int size() {
    return 0;
  }

  public Collection copyTo(Collection c) {
    return c;
  }

  public ArcObject type() {
    return TYPE;
  }

  public int hashCode() {
    return "nil".hashCode();
  }

  public boolean equals(Object other) {
    return (this == other) || (((ArcObject) other) instanceof Nil);
  }

  public Object unwrap() {
    return Boolean.FALSE;
  }

  public boolean isSame(ArcObject other) {
    return other instanceof Nil;
  }

  public ArcObject or(ArcObject other) {
    return other;
  }

  public boolean hasLen(int i) {
    return i == 0;
  }

  public boolean longerThan(int i) {
    return i < 0;
  }

  public int highestLexicalScopeReference() {
    return Integer.MIN_VALUE;
  }
}
