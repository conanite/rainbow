package rainbow.functions.system;

import rainbow.ArcError;
import rainbow.types.*;

import java.io.IOException;
import java.io.InputStream;

public class SystemFunctions {
  public static void collect() {
  }

  public static void copyStream(InputStream in, Output output) throws IOException {
    int c;
    while ((c = in.read()) != -1) {
      output.writeByte((byte) c);
    }
  }

  public static InputStream createProcess(ArcString command) throws IOException {
    return Runtime.getRuntime().exec(command.value()).getInputStream();
  }

  public static ArcObject pipeFrom(ArcString command) {
    try {
      return PipedInputPort.create(command);
    } catch (IOException e) {
      throw new ArcError("system: failed to run " + command);
    }
  }

}
