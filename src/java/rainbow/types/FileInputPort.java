package rainbow.types;

import rainbow.ArcError;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class FileInputPort extends Input {
  private ArcObject name;

  public FileInputPort(ArcObject name) {
    super(getStream(name));
    this.name = name;
  }

  private static FileInputStream getStream(ArcObject name) {
    try {
      return new FileInputStream(cast(name, ArcString.class).value());
    } catch (FileNotFoundException e) {
      throw new ArcError("File not found: " + name, e);
    }
  }

  public String getName() {
    return name.toString();
  }
}
