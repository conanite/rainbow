package rainbow.types;

import rainbow.ArcError;

public class ArcThreadLocal extends LiteralObject {
  public static final Symbol TYPE = Symbol.mkSym("thread-local");
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
