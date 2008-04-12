package rainbow.types;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

public class StringOutputPort extends Output {
  private final ByteArrayOutputStream bytes;

  private StringOutputPort(ByteArrayOutputStream bytes) {
    super(new PrintStream(bytes));
    this.bytes = bytes;
  }

  public StringOutputPort() {
    this(new ByteArrayOutputStream());
  }

  public ArcString value() {
    return ArcString.make(bytes.toString());
  }

  public String toString() {
    return "#<output-port:string>";
  }
}
