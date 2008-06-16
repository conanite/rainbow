package rainbow.types;

import rainbow.ArcError;
import rainbow.functions.Builtin;
import rainbow.parser.ArcParser;

import java.io.*;

public class Input extends ArcObject {
  public static Symbol TYPE = (Symbol) Symbol.make("input");
  private InputStream original;
  private PushbackInputStream in;
  private PushbackReader reader;
  private boolean closed = false;
  private ArcParser parser;

  public Input(InputStream in) {
    this.original = in;
  }

  public ArcObject readObject(ArcObject eof) {
    try {
      ArcObject result = getParser().parseOneLine();
      if (result == null) {
        return eof;
      } else {
        return result;
      }
    } catch (Exception e) {
      throw new ArcError("Can't parse input: " + e, e);
    }
  }

  private ArcParser getParser() {
    if (parser == null) {
      if (reader != null) {
        parser = new ArcParser(reader);
      } else {
        parser = new ArcParser(getInputStream());
      }
    }
    return parser;
  }

  public ArcObject readByte() {
    try {
      int theByte = getInputStream().read();
      if (theByte < 0) {
        return NIL;
      }
      return Rational.make(theByte);
    } catch (IOException e) {
      throw new ArcError("Cannot read byte from " + this, e);
    }
  }

  public ArcCharacter peek() {
    try {
      int c = getReader().read();
      getReader().unread(c);
      return new ArcCharacter((char)c);
    } catch (IOException e) {
      throw new ArcError(e);
    }
  }

  public ArcObject readCharacter() {
    int result;
    try {
      result = getReader().read();
    } catch (IOException e) {
      throw new ArcError("reading character: " + e);
    }
    if (result < 0) {
      return NIL;
    }
    return new ArcCharacter((char) result);
  }

  private PushbackReader getReader() {
    notClosed();
    if (in != null) {
      throw new ArcError("This Input is already used as a byte stream");
    }
    if (reader == null) {
      reader = new PushbackReader(new InputStreamReader(original));
    }
    return reader;
  }

  private void notClosed() {
    if (closed) {
      throw new ArcError("Input is closed");
    }
  }

  private PushbackInputStream getInputStream() {
    if (reader != null) {
      throw new ArcError("This Input is already used as a character stream");
    }
    if (in == null) {
      in = new PushbackInputStream(original);
    }
    return in;
  }

  public ArcObject type() {
    return TYPE;
  }

  public Object unwrap() {
    return original;
  }

  public void close() {
    try {
      closed = true;
      original.close();
    } catch (IOException e) {
      throw new ArcError("closing Input: " + e, e);
    }
  }

  public String getName() {
    return "<input>";
  }

  public static Input cast(ArcObject argument, Object caller) {
    try {
      return (Input) argument;
    } catch (ClassCastException e) {
      throw new ArcError("Wrong argument type: " + caller + " expected input-port, got " + argument);
    }
  }
}
