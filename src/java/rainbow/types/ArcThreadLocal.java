package rainbow.types;

import rainbow.functions.Builtin;
import rainbow.ArcError;

public class ArcThreadLocal extends ArcObject {
  public static final Symbol TYPE = (Symbol) Symbol.make("thread-local");
  private final ThreadLocal tl = new ThreadLocal();
  
  public void set(ArcObject value) {
    tl.set(value);
  }
  
  public ArcObject get() {
    return (ArcObject) tl.get();
  }

  public ArcObject type() {
    return TYPE;
  }

  public static ArcThreadLocal cast(ArcObject argument, Object caller) {
    try {
      return (ArcThreadLocal) argument;
    } catch (ClassCastException e) {
      throw new ArcError("Wrong argument type: " + caller + " expected a thread-local, got " + argument);
    }
  }
}
