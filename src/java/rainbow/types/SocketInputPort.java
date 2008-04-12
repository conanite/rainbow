package rainbow.types;

import java.io.InputStream;

public class SocketInputPort extends Input {
  private ArcString client;

  public SocketInputPort(InputStream in, ArcString client) {
    super(in);
    this.client = client;
  }

  public ArcString clientAddress() {
    return client;
  }
}
