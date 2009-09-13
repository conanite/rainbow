package rainbow.types;

import rainbow.ArcError;
import rainbow.vm.Instruction;

import java.util.List;

public class ArcException extends LiteralObject {
  public static Symbol TYPE = Symbol.mkSym("exn");
  private static final ArcString NO_MESSAGE = ArcString.make("no message");
  private Throwable original;
  private List stackTrace;
  private Instruction operating;

  public ArcException() {
  }

  public ArcException(Throwable e, Instruction operating, List stackTrace) {
    this.operating = operating;
    this.original = e;
    this.stackTrace = stackTrace;
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

  public Throwable getOriginal() {
    return original;
  }

  public static ArcException cast(ArcObject argument, ArcObject caller) {
    try {
      return (ArcException) argument;
    } catch (ClassCastException e) {
      throw new ArcError("Wrong argument type: " + caller + " expected an exception, got " + argument);
    }
  }

  public List getStackTrace() {
    return stackTrace;
  }

  public Instruction getOperating() {
    return operating;
  }
}
