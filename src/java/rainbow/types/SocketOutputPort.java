package rainbow.types;

import rainbow.ArcError;

import java.io.OutputStream;
import java.io.PrintStream;

public class SocketOutputPort extends Output {
  private ArcString client;

  public SocketOutputPort(OutputStream out, ArcString client) {
    super(new PrintStream(out));
    this.client = client;
  }

  public ArcString clientAddress() {
    return client;
  }

  public static SocketInputPort cast(ArcObject argument, ArcObject caller) {
    try {
      return (SocketInputPort) argument;
    } catch (ClassCastException e) {
      throw new ArcError("Wrong argument type: " + caller + " expected a SocketInputPort, got " + argument);
    }
  }
}
