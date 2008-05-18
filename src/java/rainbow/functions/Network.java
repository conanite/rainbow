package rainbow.functions;

import rainbow.types.*;

public class Network {
  public static class OpenSocket extends Builtin {
    public ArcObject invoke(Pair args) {
      ArcNumber n = ArcNumber.cast(args.car(), this);
      return new ArcSocket((int) n.toInt());
    }
  }

  public static class ClientIp extends Builtin {
    public ArcObject invoke(Pair args) {
      return SocketOutputPort.cast(args.car(), this).clientAddress();
    }
  }

  public static class SocketAccept extends Builtin {
    public ArcObject invoke(Pair args) {
      return ArcSocket.cast(args.car(), this).accept();
    }
  }
}
