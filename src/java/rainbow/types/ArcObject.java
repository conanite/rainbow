package rainbow.types;

import rainbow.*;
import rainbow.parser.Token;

public abstract class ArcObject {
  public static final Nil NIL = Nil.NIL;
  public static final Truth T = Truth.T;
  String sourceName;
  private Token source;

  public ArcObject eval(Bindings arc) {
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

  public ArcObject eqv(ArcObject other) {
    return Truth.valueOf(this == other);
  }

  public ArcObject source(String sourceName, Token source) {
    this.sourceName = sourceName;
    this.source = source;
    return this;
  }
  
  public String source() {
    return (sourceName == null ? "<unknown>" : sourceName) + ":" + (source == null ? "<unknown>" : ("" + source.beginLine + " column " + source.beginColumn));
  }

  public abstract ArcObject type();

  public ArcObject sourceFrom(ArcObject sourceInvocation) {
    return source(sourceInvocation.sourceName, sourceInvocation.source);
  }

  public ArcObject copy() {
    return this;
  }

  public static <T> T cast(ArcObject arcObject, Class<T> aClass) {
    if (aClass.isAssignableFrom(arcObject.getClass())) {
      return (T) arcObject;
    } else {
      throw new ArcError("expected " + aClass.getSimpleName() + "; got " + arcObject + " which is a " + arcObject.getClass().getSimpleName());
    }
  }
}
