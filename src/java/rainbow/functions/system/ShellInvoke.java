package rainbow.functions.system;

import rainbow.ArcError;
import rainbow.functions.Builtin;
import rainbow.functions.IO;
import rainbow.types.ArcObject;
import rainbow.types.ArcString;
import rainbow.types.Pair;

import java.io.IOException;

public class ShellInvoke extends Builtin {
  public ShellInvoke() {
    super("system");
  }

  public ArcObject invoke(Pair args) {
    try {
      SystemFunctions.copyStream(SystemFunctions.createProcess(ArcString.cast(args.car(), this)), IO.stdOut());
      return NIL;
    } catch (IOException e) {
      throw new ArcError("system: failed to run " + args.car());
    }
  }
}
