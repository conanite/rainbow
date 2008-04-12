package rainbow.types;

import rainbow.ArcError;

import java.io.FileOutputStream;
import java.io.PrintStream;
import java.io.File;
import java.io.IOException;

public class FileOutputPort extends Output {
  public FileOutputPort(ArcObject name, ArcObject appendSymbol) {
    super(openStream(name, appendSymbol));
  }

  private static PrintStream openStream(ArcObject name, ArcObject appendSymbol) {
    try {
      String path = cast(name, ArcString.class).value();
      boolean append = !appendSymbol.isNil() && cast(appendSymbol, Symbol.class).name().equals("append");
      if (!append) {
        File f = new File(path);
        f.delete();
        f.createNewFile();
      }
      return new PrintStream(new FileOutputStream(path));
    } catch (IOException e) {
      throw new ArcError("Error opening output file " + name, e);
    }
  }
}
