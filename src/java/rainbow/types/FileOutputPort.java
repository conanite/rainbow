package rainbow.types;

import rainbow.ArcError;

import java.io.FileOutputStream;
import java.io.PrintStream;
import java.io.File;
import java.io.IOException;

public class FileOutputPort extends Output {
  public FileOutputPort(String name, boolean append) {
    super(openStream(name, append));
  }

  private static PrintStream openStream(String path, boolean append) {
    try {
      if (!append) {
        File f = new File(path);
        f.delete();
        f.createNewFile();
      }
      return new PrintStream(new FileOutputStream(path));
    } catch (IOException e) {
      throw new ArcError("Error opening output file " + path, e);
    }
  }
}
