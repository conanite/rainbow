package rainbow.functions;

import rainbow.types.*;

public class Network {
  public static class OpenSocket extends Builtin {
    public ArcObject invoke(Pair args) {
      ArcNumber n = cast(args.car(), ArcNumber.class);
      return new ArcSocket((int) n.toInt());
    }
  }

  public static class ClientIp extends Builtin {
    public ArcObject invoke(Pair args) {
      return cast(args.car(), SocketOutputPort.class).clientAddress();
    }
  }

  public static class SocketAccept extends Builtin {
    public ArcObject invoke(Pair args) {
      return cast(args.car(), ArcSocket.class).accept();
    }
  }
}
