package rainbow.types;

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
}
