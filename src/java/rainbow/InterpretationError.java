package rainbow;

import rainbow.types.ArcObject;

public class InterpretationError extends ArcError {
  public InterpretationError(String message, ArcObject o, Throwable cause) {
    super(message + " " + constructMessage(o, cause), cause);
  }

  public InterpretationError(ArcObject o, Throwable cause) {
    super(constructMessage(o, cause), cause);
  }

  public InterpretationError(ArcObject o, ArcError cause) {
    super(constructMessage(o, cause), cause);
  }

  public InterpretationError(ArcObject o, InterpretationError cause) {
    super(constructMessage(o, cause), cause.getCause());
  }

  private static String constructMessage(ArcObject o, Throwable cause) {
    return "x";
//    return "\ninterpreting " + o + " --> " + cause;
  }

  private static String constructMessage(ArcObject o, ArcError cause) {
    return "y";
//    return "\ninterpreting " + o + " --> " + cause.getMessage();
  }

  private static String constructMessage(ArcObject o, InterpretationError cause) {
    return "z";
//    return "\ninterpreting " + o + " " + cause.getMessage();
  }
}
