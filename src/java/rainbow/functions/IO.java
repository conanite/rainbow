package rainbow.functions;

import rainbow.ArcError;
import rainbow.Nil;
import rainbow.types.*;

public abstract class IO {
  public static Output STD_OUT = new Output(System.out) {
    public void close() {
    }

    public String toString() {
      return "IO.STD_OUT";
    }
  };
  public static Output STD_ERR = new Output(System.err) {
    public void close() {
    }

    public String toString() {
      return "IO.STD_ERR";
    }
  };
  public static Input STD_IN = new Input(System.in) {
    public void close() {
    }

    public String toString() {
      return "IO.STD_IN";
    }
  };

  public static final ThreadLocal<Input> stdIn = new ThreadLocal() {
    protected Object initialValue() {
      return STD_IN;
    }
  };

  public static final ThreadLocal<Output> stdOut = new ThreadLocal() {
    protected Object initialValue() {
      return STD_OUT;
    }
  };

  public static Input stdIn() {
    return stdIn.get();
  }

  public static Output stdOut() {
    return stdOut.get();
  }

  public static void close(ArcObject port) {
    if (port instanceof Input) {
      ((Input) port).close();
    } else if (port instanceof Output) {
      ((Output) port).close();
    } else if (port instanceof ArcSocket) {
      ((ArcSocket) port).close();
    } else if (port instanceof JavaObject) {
      ((JavaObject) port).close();
    } else {
      throw new ArcError("close: expected Input or Output object; got " + port);
    }
  }

  public static Output chooseOutputPort(ArcObject port, Object caller) {
    if (port instanceof Nil) {
      return stdOut();
    } else {
      return Output.cast(port, caller);
    }
  }

  public static Input chooseInputPort(ArcObject port, Object caller) {
    if (port instanceof Nil) {
      return stdIn();
    } else {
      return Input.cast(port, caller);
    }
  }

  public static ArcObject closeAll(Pair args) {
    while (!(args instanceof Nil)) {
      close(args.car());
      args = (Pair) args.cdr();
    }
    return ArcObject.NIL;
  }
}
