package rainbow.types;

import rainbow.ArcError;
import rainbow.Nil;
import rainbow.vm.VM;

import java.util.*;

public class Pair extends ArcObject {
  public static final Symbol TYPE = Symbol.mkSym("cons");

  private static final Map specials = new HashMap();

  static {
    specials.put("quasiquote", "`");
    specials.put("quote", "'");
    specials.put("unquote", ",");
    specials.put("unquote-splicing", ",@");
  }

  private ArcObject car;
  private ArcObject cdr;

  public Pair() {
  }

  public Pair(ArcObject car, ArcObject cdr) {
    if (car == null) {
      throw new ArcError("Can't create Pair with null car: use NIL instead");
    }
    if (cdr == null) {
      throw new ArcError("Can't create Pair with null cdr: use NIL instead");
    }
    this.car = car;
    this.cdr = cdr;
  }

  public void invoke(VM vm, Pair args) {
    ArcNumber index = ArcNumber.cast(args.car(), this);
    vm.pushA(this.nth(index.toInt()).car());
  }

  public ArcObject xcar() {
    return car;
  }

  public ArcObject car() {
    return car;
  }

  public ArcObject cdr() {
    return cdr;
  }

  public void mustBeNil() throws NotNil {
    if (car != null) {
      throw new NotNil();
    }
  }

  public boolean isNotPair() {
    return false;
  }

  public ArcObject scar(ArcObject newCar) {
    return this.car = newCar;
  }

  public ArcObject sref(ArcObject value, ArcObject idx) {
    Rational index = Rational.cast(idx, this);
    long n = index.toInt();
    return nth(n).scar(value);
  }

  public ArcObject sref(Pair args) {
    return sref(args.car(), args.cdr().car());
  }

  public boolean isCar(Symbol s) {
    return s == car;
  }

  public String toString() {
    if (isSpecial()) {
      Symbol s = (Symbol) car();
      return specials.get(s.name()) + ((Pair)cdr()).internalToString();
    } else {
      return "(" + internalToString() + ")";
    }
  }

  private String internalToString() {
    return internalToString(this);
  }

  private static String internalToString(ArcObject o) {
    StringBuffer result = new StringBuffer();
    while (!o.isNotPair()) {
      result.append(o.car());
      if (!o.cdr().isNotPair()) {
        result.append(" ");
      }
      o = o.cdr();
    }

    if (!(o instanceof Nil)) {
      result.append(" . ");
      result.append(o);
    }
    return result.toString();
  }

  public void setCdr(ArcObject cdr) {
    this.cdr = cdr;
  }

  public static Pair parse(List items) {
    if (items == null || items.size() == 0) {
      return NIL;
    }

    if (items.get(0) == Symbol.DOT) {
      return illegalDot(items);
    }

    return new Pair((ArcObject) items.get(0), internalParse(items.subList(1, items.size())));
  }

  private static ArcObject internalParse(List items) {
    if (items.size() == 0) {
      return NIL;
    }

    if (items.get(0) == Symbol.DOT) {
      if (items.size() == 2) {
        return (ArcObject) items.get(1);
      } else {
        return illegalDot(items);
      }
    }

    return new Pair((ArcObject) items.get(0), internalParse(items.subList(1, items.size())));
  }

  private static Pair illegalDot(List items) {
    throw new ArcError("Error: illegal use of \".\" in " + items);
  }

  private static ArcObject buildFrom(List items, ArcObject last, int n) {
    if (n == items.size()) {
      return last;
    } else {
      return new Pair((ArcObject) items.get(n), buildFrom(items, last, n+1));
    }
  }

  public static ArcObject buildFrom(List items, ArcObject last) {
    if (items == null || items.size() == 0) {
      return last;
    }

    return buildFrom(items, last, 0);
  }

  public static Pair buildFrom(List items) {
    return (Pair)buildFrom(items, NIL);
  }

  public static Pair buildFrom(ArcObject... items) {
    return (Pair)buildFrom(Arrays.asList(items), NIL);
  }

  public ArcObject type() {
    return TYPE;
  }

  public long len() {
    return size();
  }

  public int improperLen() {
    return improperLen(this);
  }

  public static int improperLen(ArcObject o) {
    int count = 0;
    while (!o.isNotPair()) {
      count++;
      o = o.cdr();
    }
    return count;
  }

  public int size() {
    if (this instanceof Nil) {
      return 0;
    } else {
      int result = 1;
      ArcObject rest = cdr;
      while (!(rest instanceof Nil)) {
        if (rest.isNotPair()) {
          throw new ArcError("cannot take size: not a proper list: " + this);
        }
        result++;
        rest = rest.cdr();
      }
      return result;
    }
  }

  public int compareTo(ArcObject right) {
    throw new ArcError("Pair.compareTo:unimplemented");
  }

  public Collection copyTo(Collection c) {
    c.add(car());
    if (cdr() instanceof Nil) {
      return c;
    } else if (!(cdr() instanceof Pair)) {
      throw new ArcError("Not a list: " + this);
    }
    cdr().copyTo(c);
    return c;
  }

  public boolean equals(Object other) {
    if ((this == other)) {
      return true;
    } else {
      boolean iAmNil = this instanceof Nil;
      boolean itIsNil = ((ArcObject) other) instanceof Nil;
      if ((iAmNil != itIsNil)) {
        return false;
      } else if (iAmNil && itIsNil) {
        return true;
      } else {
        boolean isPair = other instanceof Pair;
        if (isPair) {
          boolean eqCar = ((Pair) other).car.equals(car);
          boolean eqCdr = ((Pair) other).cdr.equals(cdr);
          return (eqCar && eqCdr);
        } else {
          return false;
        }
      }
    }
  }

  public int hashCode() {
    return car().hashCode() + (37 * cdr().hashCode());
  }

  public Pair nth(long index) {
    try {
      return (Pair) nth(this, index);
    } catch (OOB oob) {
      throw new ArcError("Error: index " + index + " too large for list " + this);
    }
  }

  private static ArcObject nth(ArcObject p, long index) {
    while (index > 0) {
      if (p.cdr() instanceof Nil) {
        throw new OOB();
      }
      p = p.cdr();
      index--;
    }
    return p;
  }

  public boolean hasLen(int i) {
    return cdr().hasLen(i - 1);
  }

  public boolean longerThan(int i) {
    return i < 0 || cdr().longerThan(i - 1);
  }

  static class OOB extends RuntimeException {
  }

  public boolean isSpecial() {
    return car() instanceof Symbol &&
            specials.containsKey(((Symbol) car()).name()) &&
            cdr() instanceof Pair &&
            ((Pair)cdr()).size() == 1;
  }

  public Pair copy() {
    if (this instanceof Nil) {
      return this;
    }
    return new Pair(car(), cdr().copy());
  }

  public Object unwrap() {
    List result = new ArrayList(size());
    unwrapList(result, this);
    return result;
  }

  public boolean isSame(ArcObject other) {
    return super.isSame(other) || ((this instanceof Nil) && (other instanceof Nil));
  }

  private static void unwrapList(List result, Pair list) {
    if (list instanceof Nil) {
      return;
    }
    result.add(list.car().unwrap());
    unwrapList(result, (Pair) list.cdr());
  }

  public ArcObject[] toArray() {
    ArcObject[] result = new ArcObject[size()];
    int i = 0;
    toArray(result, i);
    return result;
  }

  private void toArray(ArcObject[] result, int i) {
    if (i < result.length) {
      result[i] = car;
      if (!(cdr instanceof Nil)) {
        ((Pair)cdr).toArray(result, i + 1);
      }
    }
  }

  public static Pair cast(ArcObject argument, Object caller) {
    try {
      return (Pair) argument;
    } catch (ClassCastException e) {
      throw new ArcError("Wrong argument type: " + caller + " expected a cons, got " + argument);
    }
  }

  public void mustBePairOrNil() throws NotPair {
  }

  public static Pair append(Pair pair, ArcObject value) {
    if (pair == null) {
      return new Pair(value, NIL);
    } else {
      Pair test = pair;
      while (!(test.cdr instanceof Nil)) {
        test = (Pair) test.cdr;
      }
      test.cdr = new Pair(value, NIL);
    }
    return pair;
  }

  public ArcObject rev() {
    return null;
  }

  public boolean isProper() {
    return isProper(this);
  }

  public int highestLexicalScopeReference() {
    int hcar = car.highestLexicalScopeReference();
    int hcdr = cdr.highestLexicalScopeReference();
    return hcar > hcdr ? hcar : hcdr;
  }

  public static boolean isProper(ArcObject pair) {
    while (!pair.isNotPair()) {
      pair = pair.cdr();
    }
    return pair instanceof Nil;
  }

  public static class NotPair extends Throwable {
  }
}
