package rainbow.types;

import rainbow.types.ArcObject;
import rainbow.*;
import rainbow.functions.Builtin;
import rainbow.vm.ArcThread;
import rainbow.vm.Continuation;
import rainbow.vm.continuations.FunctionBodyBuilder;

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

  public void setCar(ArcObject item) {
    this.car = item;
  }

  public void setCdr(ArcObject cdr) {
    this.cdr = cdr;
  }

  public boolean isNil() {
    return car == null && cdr == null;
  }

  public static Pair buildFrom(List items, ArcObject last) {
    Pair pair = new Pair();
    if (items == null) {
      return pair;
    }
    if (items.size() != 0) {
      pair.car = (ArcObject) items.get(0);
      if (items.size() == 1) {
        pair.cdr = last;
      } else {
        pair.cdr = buildFrom(items.subList(1, items.size()), last);
      }
    }
    return pair;
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

  public int size() {
    if (isNil()) {
      return 0;
    } else if (cdr instanceof Pair) {
      return 1 + ((Pair)cdr).size();
    } else {
      throw new ArcError("cannot take size: not a proper list: " + this);
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
          if ((eqCar && eqCdr)) {
            return true;
          } else {
            return false;
          }
        } else {
          return false;
        }
      }
    }
  }

  public int hashCode() {
    return car.hashCode() + (37 * cdr().hashCode());
  }

  public String code() {
    return "<pair>";
  }

  public Pair nth(long index) {
    if (index == 0) {
      return this;
    } else {
      return ((Pair) cdr()).nth(index - 1);
    }
  }

  public boolean isSpecial() {
    return car() instanceof Symbol && specials.containsKey(((Symbol) car()).name()) && cdr() instanceof Pair;
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
}
