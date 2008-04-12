package rainbow.types;

import java.io.ByteArrayInputStream;

public class StringInputPort extends Input {
  public StringInputPort(String s) {
    super(new ByteArrayInputStream(s.getBytes()));
  }
}
