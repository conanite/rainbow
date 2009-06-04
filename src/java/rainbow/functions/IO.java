package rainbow.functions;

import rainbow.*;
import rainbow.types.*;
import rainbow.vm.ArcThread;
import rainbow.vm.Continuation;
import rainbow.vm.continuations.CallWStdinContinuation;
import rainbow.vm.continuations.CallWStdoutContinuation;

public class IO {
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

  public static void collect(Environment top) {
    top.add(new Builtin[]{
      new Builtin("call-w/stdin") {
        public void invoke(ArcThread thread, LexicalClosure lc, final Continuation caller, Pair args) {
          final Input previous = thread.swapStdIn(Input.cast(args.car(), this));
          final Function thunk = Builtin.cast(args.cdr().car(), this);
          thunk.invoke(thread, lc, new CallWStdinContinuation(thread, lc, caller, previous), NIL);
        }
      }, new Builtin("call-w/stdout") {
        public void invoke(ArcThread thread, LexicalClosure lc, final Continuation caller, Pair args) {
          final Output previous = thread.swapStdOut(Output.cast(args.car(), this));
          final Function thunk = Builtin.cast(args.cdr().car(), this);
          thunk.invoke(thread, lc, new CallWStdoutContinuation(thread, lc, caller, previous), NIL);
        }
      }, new Builtin("stdin") {
        public void invoke(ArcThread thread, LexicalClosure lc, Continuation caller, Pair args) {
          caller.receive(thread.stdIn());
        }
      }, new Builtin("stdout") {
        public void invoke(ArcThread thread, LexicalClosure lc, Continuation caller, Pair args) {
          caller.receive(thread.stdOut());
        }
      }, new Builtin("stderr") {
        public ArcObject invoke(Pair args) {
          return STD_ERR;
        }
      }, new Builtin("disp") {
        public void invoke(ArcThread thread, LexicalClosure lc, Continuation caller, Pair args) {
          Output out = chooseOutputPort(args.cdr().car(), thread, this);
          ArcObject o = args.car();
          if (o instanceof ArcString) {
            out.write(((ArcString) o).value());
          } else if (o instanceof ArcCharacter) {
            out.writeChar((ArcCharacter) o);
          } else {
            out.write(o);
          }
          caller.receive(NIL);
        }
      }, new Builtin("write") {
        public void invoke(ArcThread thread, LexicalClosure lc, Continuation caller, Pair args) {
          chooseOutputPort(args.cdr().car(), thread, this).write(args.car());
          caller.receive(NIL);
        }
      }, new Builtin("sread") {
        public ArcObject invoke(Pair args) {
          return Input.cast(args.car(), this).readObject(args.cdr().car());
        }
      }, new Builtin("writeb") {
        public void invoke(ArcThread thread, LexicalClosure lc, Continuation caller, Pair args) {
          chooseOutputPort(args.cdr().car(), thread, this).writeByte(Rational.cast(args.car(), this));
          caller.receive(NIL);
        }
      }, new Builtin("writec") {
        public void invoke(ArcThread thread, LexicalClosure lc, Continuation caller, Pair args) {
          chooseOutputPort(args.cdr().car(), thread, this).writeChar(ArcCharacter.cast(args.car(), this));
          caller.receive(NIL);
        }
      }, new Builtin("readb") {
        public void invoke(ArcThread thread, LexicalClosure lc, Continuation caller, Pair args) {
          caller.receive(chooseInputPort(args.car(), thread, this).readByte());
        }
      }, new Builtin("readc") {
        public void invoke(ArcThread thread, LexicalClosure lc, Continuation caller, Pair args) {
          caller.receive(chooseInputPort(args.car(), thread, this).readCharacter());
        }
      }, new Builtin("close") {
        public ArcObject invoke(Pair args) {
          while (!args.isNil()) {
            close(args.car());
            args = (Pair) args.cdr();
          }
          return NIL;
        }
      },
    });
  }

  private static void close(ArcObject port) {
    if (port instanceof Input) {
      ((Input) port).close();
    } else if (port instanceof Output) {
      ((Output) port).close();
    } else {
      throw new ArcError("close: expected Input or Output object; got " + port);
    }
  }

  private static Output chooseOutputPort(ArcObject port, ArcThread thread, Object caller) {
    Output out;
    if (!port.isNil()) {
      out = Output.cast(port, caller);
    } else {
      out = thread.stdOut();
    }
    return out;
  }

  private static Input chooseInputPort(ArcObject port, ArcThread thread, Object caller) {
    Input in;
    if (!port.isNil()) {
      in = Input.cast(port, caller);
    } else {
      in = thread.stdIn();
    }
    return in;
  }

}
