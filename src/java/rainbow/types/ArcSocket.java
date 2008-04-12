package rainbow.types;

import rainbow.ArcError;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class ArcSocket extends ArcObject {
  public static final ArcObject TYPE = Symbol.make("socket");
  private ServerSocket ss;

  public ArcObject type() {
    return TYPE;
  }

  public ArcSocket(int port) {
    try {
      this.ss = new ServerSocket(port);
    } catch (IOException e) {
      throw new ArcError("open-socket: unable top open listener on port " + port + ": " + e.getMessage(), e);
    }
  }

  public Pair accept() {
    try {
      Socket socket = ss.accept();
      InputStream in = socket.getInputStream();
      OutputStream out = socket.getOutputStream();
      ArcString address = ArcString.make(socket.getInetAddress().toString());
      return Pair.buildFrom(
              new SocketInputPort(in, address),
              new SocketOutputPort(out, address),
              address);
    } catch (IOException e) {
      throw new ArcError("socket-accept: " + ss.getLocalPort() + " failed : " + e.getMessage(), e);
    }
  }

  public String toString() {
    return TYPE + " " + ss;
  }
}
