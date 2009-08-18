package rainbow.functions.network;

import rainbow.functions.Builtin;
import rainbow.types.ArcObject;
import rainbow.types.Pair;
import rainbow.types.SocketOutputPort;

public class ClientIp extends Builtin {
  public ClientIp() {
    super("client-ip");
  }

  public ArcObject invoke(Pair args) {
    return SocketOutputPort.cast(args.car(), this).clientAddress();
  }
}
