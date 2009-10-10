package rainbow.types;

import rainbow.ArcError;

import java.io.OutputStream;
import java.io.PrintStream;

public class SocketOutputPort extends Output {

  public SocketOutputPort(OutputStream out) {
    super(new PrintStream(out));
  }

  public static SocketInputPort cast(ArcObject argument, ArcObject caller) {
    try {
      return (SocketInputPort) argument;
    } catch (ClassCastException e) {
      throw new ArcError("Wrong argument type: " + caller + " expected a SocketInputPort, got " + argument);
    }
  }
}
