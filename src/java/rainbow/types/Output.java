package rainbow.types;

import rainbow.ArcError;
import rainbow.functions.Builtin;

import java.io.PrintStream;

public class Output extends ArcObject {
  public static Symbol TYPE = (Symbol) Symbol.make("output");
  private PrintStream out;

  public Output(PrintStream out) {
    this.out = out;
  }

  public void write(String s) {
    out.print(s);
  }

  public void write(ArcObject arcObject) {
    out.print(arcObject);
  }

  public ArcObject type() {
    return TYPE;
  }

  public Object unwrap() {
    return out;
  }

  public void writeByte(Rational rational) {
    if (!rational.isInteger()) {
      throw new ArcError("write byte: expected byte, got " + rational);
    }
    writeByte((byte) rational.toInt());
  }

  public void writeChar(ArcCharacter arcCharacter) {
    out.print(arcCharacter.value());
  }

  public void close() {
    out.close();
  }

  public void writeByte(byte b) {
    out.write(b);
  }

  public static Output cast(ArcObject argument, Object caller) {
    try {
      return (Output) argument;
    } catch (ClassCastException e) {
      throw new ArcError("Wrong argument type: " + caller + " expected output-port, got " + argument);
    }
  }
}
