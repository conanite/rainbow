package rainbow.types;

import rainbow.ArcError;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class FileInputPort extends Input {
  private String name;

  public FileInputPort(String name) {
    super(getStream(name));
    this.name = name;
  }

  private static FileInputStream getStream(String name) {
    try {
      return new FileInputStream(name);
    } catch (FileNotFoundException e) {
      throw new ArcError("File not found: " + name, e);
    }
  }

  public String getName() {
    return name;
  }
}
