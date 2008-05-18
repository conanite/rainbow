package rainbow.types;

import rainbow.functions.StringIO;
import rainbow.ArcError;

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

  public static StringOutputPort cast(ArcObject argument, ArcObject caller) {
    try {
      return (StringOutputPort) argument;
    } catch (ClassCastException e) {
      throw new ArcError("Wrong argument type: " + caller + " expected a StringOutputPort, got " + argument);
    }
  }
}
