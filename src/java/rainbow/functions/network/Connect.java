package rainbow.functions.network;

import rainbow.ArcError;
import rainbow.functions.Builtin;
import rainbow.types.*;
import rainbow.vm.VM;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class Connect extends Builtin {
  public Connect() {
    super("client-socket");
  }

  public void invokef(VM vm, ArcObject arg1, ArcObject arg2) {
    String host = ((ArcString)arg1).value();
    int port = (int) ((ArcNumber)arg2).toInt();
    Socket socket;
    try {
      socket = new Socket(host, port);
      InputStream in = socket.getInputStream();
      OutputStream out = socket.getOutputStream();
      vm.pushA(Pair.buildFrom(
              new SocketInputPort(in, (ArcString) arg1),
              new SocketOutputPort(out)));
    } catch (IOException e) {
      throw new ArcError("Couldn't connect to " + arg1 + ":" + arg2 + ", " + e, e);
    }

  }

  public void invoke(VM vm, Pair args) {
    checkMaxArgCount(args, getClass(), 2);
    invokef(vm, args.car(), args.cdr().car());
  }
}
