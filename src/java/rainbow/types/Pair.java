package rainbow.types;

import rainbow.ArcError;
import rainbow.Function;
import rainbow.LexicalClosure;
import rainbow.functions.Builtin;
import rainbow.vm.ArcThread;
import rainbow.vm.Continuation;
import rainbow.vm.continuations.FunctionDispatcher;

import java.util.*;

public class Pair extends ArcObject {
  public static final Symbol TYPE = (Symbol) Symbol.make("cons");

  public static final Function REF = new Function() {
    public void invoke(ArcThread thread, LexicalClosure lc, Continuation caller, Pair args) {
      Pair pair = Pair.cast(args.car(), this);
      ArcNumber index = ArcNumber.cast(args.cdr().car(), this);
      caller.receive(pair.nth(index.toInt()).car());
    }

    public String toString() {
      return "pair-ref";
    }
  };

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

  public ArcObject car() {
    return car == null ? NIL : car;
  }

  public ArcObject cdr() {
    return cdr == null ? NIL : cdr;
  }

  public Function refFn() {
    return Pair.REF;
  }

  public void mustBeNil() throws NotNil {
    if (car != null) {
      throw new NotNil();
    }
  }

  public boolean isNotPair() {
    return isNil();
  }

  public void interpret(ArcThread thread, LexicalClosure lc, Continuation caller) {
    if (car == null) {
      caller.receive(this);
    } else {
      thread.continueWith(new FunctionDispatcher(thread, lc, caller, this));
    }
  }

  public ArcObject scar(ArcObject newCar) {
    return this.car = newCar;
  }

  public ArcObject sref(Pair args) {
    Rational index = Rational.cast(args.cdr().car(), this);
    long n = index.toInt();
    if (n >= size()) {
      throw new ArcError("sref: cannot set index " + index + " of list with " + size() + " elements");
    }
    return nth(n).scar(args.car());
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
    if (isNil()) {
      return "";
    }
    if (car == null) {
      throw new Error("Can't have null car and non-null cdr: " + cdr);
    }
    if (cdr instanceof Pair) {
      Pair rest = (Pair) cdr;
      if (rest.isNil()) {
        return toString(car);
      } else {
        return toString(car) + " " + rest.internalToString();
      }
    } else if (cdr.isNil()) {
      return toString(car);
    } else {
      return toString(car) + " . " + toString(cdr);
    }
  }

  private String toString(ArcObject object) {
    return (car instanceof Builtin ? car.getClass().getSimpleName() : object.toString());
  }

  public void setCdr(ArcObject cdr) {
    this.cdr = cdr;
  }

  public boolean isNil() {
    return car == null && cdr == null;
  }

  public static Pair parse(List items) {
    Pair pair = new Pair();
    if (items == null || items.size() == 0) {
      return pair;
    }

    if (items.get(0) == Symbol.DOT) {
      return illegalDot(items);
    }

    pair.car = (ArcObject) items.get(0);
    pair.cdr = internalParse(items.subList(1, items.size()));
    return pair;
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

    Pair pair = new Pair();
    pair.car = (ArcObject) items.get(0);
    pair.cdr = internalParse(items.subList(1, items.size()));
    return pair;
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

  public static Pair buildFrom(List items, ArcObject last) {
    if (items == null) {
      return new Pair();
    }

    return (Pair)buildFrom(items, last, 0);
  }

  public static Pair buildFrom(List items) {
    return buildFrom(items, NIL);
  }

  public static Pair buildFrom(ArcObject... items) {
    return buildFrom(Arrays.asList(items), NIL);
  }

  public ArcObject type() {
    return isNil() ? NIL.type() : TYPE;
  }

  public long len() {
    return size();
  }

  public int size() {
    if (isNil()) {
      return 0;
    } else {
      int result = 1;
      Pair rest = (Pair) cdr;
      while (!rest.isNil()) {
        result++;
        if (!(rest.cdr() instanceof Pair)) {
          throw new ArcError("cannot take size: not a proper list: " + this);
        }
        rest = (Pair) rest.cdr();
      }
      return result;
    }
  }

  public int compareTo(ArcObject right) {
    throw new ArcError("Pair.compareTo:unimplemented");
  }

  public Collection copyTo(Collection c) {
    if (isNil()) {
      return c;
    }
    c.add(car());
    if (cdr().isNil()) {
      return c;
    } else if (!(cdr() instanceof Pair)) {
      throw new ArcError("Not a list: " + this);
    }
    ((Pair)cdr()).copyTo(c);
    return c;
  }

  public boolean equals(Object other) {
    if ((this == other)) {
      return true;
    } else {
      boolean iAmNil = isNil();
      boolean itIsNil = ((ArcObject) other).isNil();
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
    if (index == 0) {
      return this;
    } else {
      return ((Pair) cdr()).nth(index - 1);
    }
  }

  public boolean isSpecial() {
    return car() instanceof Symbol &&
            specials.containsKey(((Symbol) car()).name()) &&
            cdr() instanceof Pair &&
            ((Pair)cdr()).size() == 1;
  }

  public Pair copy() {
    if (isNil()) {
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
    return super.isSame(other) || isNil() && other.isNil();
  }

  private static void unwrapList(List result, Pair list) {
    if (list.isNil()) {
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
      ((Pair)cdr).toArray(result, i + 1);
    }
  }

  public static Pair cast(ArcObject argument, Object caller) {
    try {
      return (Pair) argument;
    } catch (ClassCastException e) {
      throw new ArcError("Wrong argument type: " + caller + " expected a cons, got " + argument);
    }
  }

  public void mustBePair() throws NotPair {
  }

  public static Pair append(Pair pair, ArcObject value) {
    if (pair == null) {
      return new Pair(value, NIL);
    } else {
      Pair test = pair;
      while (!test.cdr.isNil()) {
        test = (Pair) test.cdr;
      }
      test.cdr = new Pair(value, NIL);
    }
    return pair;
  }

  public static class NotPair extends Throwable {
  }
}
