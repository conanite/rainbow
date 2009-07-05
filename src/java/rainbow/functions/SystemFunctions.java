package rainbow.functions;

import rainbow.ArcError;
import rainbow.Environment;
import rainbow.LexicalClosure;
import rainbow.types.*;
import rainbow.vm.Continuation;

import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

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
    }, new Builtin("datetime") {
      public ArcObject invoke(Pair args) {
        Calendar c = new GregorianCalendar();
        if (!args.isNil()) {
          c.setTime(new Date(1000 * Rational.cast(args.car(), this).toInt()));
        }
        return Pair.buildFrom(
                Rational.make(c.get(Calendar.SECOND)),
                Rational.make(c.get(Calendar.MINUTE)),
                Rational.make(c.get(Calendar.HOUR_OF_DAY)),
                Rational.make(c.get(Calendar.DAY_OF_MONTH)),
                Rational.make(c.get(Calendar.MONTH)),
                Rational.make(c.get(Calendar.YEAR))
        );
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
      public void invoke(LexicalClosure lc, Continuation caller, Pair args) {
        pipeFrom(ArcString.cast(args.car(), this), caller);
      }
    }, new Builtin("system") {
      public void invoke(LexicalClosure lc, Continuation caller, Pair args) {
        try {
          copyStream(createProcess(ArcString.cast(args.car(), this)), caller.thread().stdOut());
          caller.receive(NIL);
        } catch (IOException e) {
          throw new ArcError("system: failed to run " + args.car());
        }
      }
    }, new Builtin("which-os") {
      public ArcObject invoke(Pair args)  {
        return Symbol.make(System.getProperty("os.name").replaceAll(" ", "").toLowerCase());
      }
    }, new Builtin("declare") {
      public ArcObject invoke(Pair args)  {
        return NIL;
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

  private static void pipeFrom(ArcString command, Continuation caller) {
    try {
      caller.receive(PipedInputPort.create(command));
    } catch (IOException e) {
      throw new ArcError("system: failed to run " + command);
    }
  }
}
