package rainbow.functions.network;

import rainbow.functions.Builtin;
import rainbow.types.ArcObject;
import rainbow.types.Pair;
import rainbow.types.ArcSocket;

public class SocketAccept extends Builtin {
  public SocketAccept() {
    super("socket-accept");
  }

  public ArcObject invoke(Pair args) {
    return ArcSocket.cast(args.car(), this).accept();
  }
}
