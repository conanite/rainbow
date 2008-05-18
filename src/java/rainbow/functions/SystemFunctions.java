package rainbow.functions;

import rainbow.ArcError;
import rainbow.Environment;
import rainbow.LexicalClosure;
import rainbow.types.*;
import rainbow.vm.ArcThread;
import rainbow.vm.Continuation;

import java.io.IOException;
import java.io.InputStream;

public class SystemFunctions {
  public static void collect(Environment top) {
    top.add(new Builtin[]{
            new Builtin("msec") {
              public ArcObject invoke(Pair args) {
                return Rational.make(System.currentTimeMillis());
              }
            }, new Builtin("seconds") {
      public ArcObject invoke(Pair args) {
        return Rational.make(System.currentTimeMillis() / 1000);
      }
    }, new Builtin("current-process-milliseconds") {
      public ArcObject invoke(Pair args) {
        return Rational.make(1);
      }
    }, new Builtin("current-gc-milliseconds") {
      public ArcObject invoke(Pair args) {
        return Rational.make(1);
      }
    }, new Builtin("pipe-from") {
      public void invoke(ArcThread thread, LexicalClosure lc, Continuation whatToDo, Pair args) {
        pipeFrom(ArcString.cast(args.car(), this), whatToDo);
      }
    }, new Builtin("system") {
      public void invoke(ArcThread thread, LexicalClosure lc, Continuation whatToDo, Pair args) {
        try {
          copyStream(createProcess(ArcString.cast(args.car(), this)), thread.stdOut());
          whatToDo.receive(NIL);
        } catch (IOException e) {
          throw new ArcError("system: failed to run " + args.car());
        }
      }
    }, new Builtin("quit") {
      public ArcObject invoke(Pair args) {
        System.exit(0);
        return null;
      }
    },
    });
  }

  private static void copyStream(InputStream in, Output output) throws IOException {
    int c;
    while ((c = in.read()) != -1) {
      output.writeByte((byte) c);
    }
  }

  public static InputStream createProcess(ArcString command) throws IOException {
    return Runtime.getRuntime().exec(command.value()).getInputStream();
  }

  private static void pipeFrom(ArcString command, Continuation whatToDo) {
    try {
      whatToDo.receive(PipedInputPort.create(command));
    } catch (IOException e) {
      throw new ArcError("system: failed to run " + command);
    }
  }
}
