package rainbow.types;

public class ArcException extends ArcObject {
  public static Symbol TYPE = (Symbol) Symbol.make("exn");
  private static final ArcString NO_MESSAGE = ArcString.make("no message");
  private Throwable original;

  public ArcException() {
  }

  public ArcException(Throwable original) {
    this.original = original;
  }

  public ArcObject type() {
    return TYPE;
  }

  public ArcObject message() {
    if (original != null) {
      return ArcString.make(original.getMessage());
    } else {
      return NO_MESSAGE;
    }
  }

  public String toString() {
    return message().toString();
  }
}
