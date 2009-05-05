package rainbow.types;

import rainbow.ArcError;
import rainbow.Environment;
import rainbow.Nil;
import rainbow.Truth;

public abstract class ArcObject {
  public static final Nil NIL = Nil.NIL;
  public static final Truth T = Truth.T;

  public Hash source;

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

  public void source(Hash source) {
    this.source = source;
  }

  public Hash source() {
    if (source == null) {
      source = new Hash();
    }
    return source;
  }

  public boolean isNotPair() {
    return isNil() || !(this instanceof Pair);
  }
}
