package rainbow.functions.network;

import rainbow.functions.Builtin;
import rainbow.types.ArcObject;
import rainbow.types.Pair;
import rainbow.types.ArcNumber;
import rainbow.types.ArcSocket;

public class OpenSocket extends Builtin {
  public OpenSocket() {
    super("open-socket");
  }

  public ArcObject invoke(Pair args) {
    ArcNumber n = ArcNumber.cast(args.car(), this);
    return new ArcSocket((int) n.toInt());
  }
}
