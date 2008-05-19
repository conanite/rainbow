package rainbow.types;

import rainbow.*;
import rainbow.parser.Token;

public abstract class ArcObject {
  public static final Nil NIL = Nil.NIL;
  public static final Truth T = Truth.T;

  public ArcObject eval(Environment env) {
    return this;
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

  public ArcObject eqv(ArcObject other) {
    return Truth.valueOf(this == other);
  }

  public abstract ArcObject type();

  public ArcObject copy() {
    return this;
  }

  public Object unwrap() {
    return this;
  }
}
