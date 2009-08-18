package rainbow.types;

import rainbow.functions.system.SystemFunctions;

import java.io.InputStream;
import java.io.IOException;

public class PipedInputPort extends Input {
  private final String command;

  public PipedInputPort(String command, InputStream in) {
    super(in);
    this.command = command;
  }

  public String getName() {
    return "<piped-from: " + command + ">";
  }

  public static PipedInputPort create(ArcString command) throws IOException {
    return new PipedInputPort(command.value(), SystemFunctions.createProcess(command));
  }
}
