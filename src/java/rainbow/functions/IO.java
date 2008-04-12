package rainbow.functions;

import rainbow.ArcError;
import rainbow.Bindings;
import rainbow.Function;
import rainbow.TopBindings;
import rainbow.types.*;
import rainbow.vm.ArcThread;
import rainbow.vm.Continuation;
import rainbow.vm.continuations.CallWStdinContinuation;
import rainbow.vm.continuations.CallWStdoutContinuation;

public class IO {
  public static Output STD_OUT = new Output(System.out) {
    public void close() {
    }
  };
  public static Output STD_ERR = new Output(System.err) {
    public void close() {
    }
  };
  public static Input STD_IN = new Input(System.in) {
    public void close() {
    }
  };

  public static void collect(TopBindings top) {
    top.add(new Builtin[]{
      new Builtin("call-w/stdin") {
        public void invoke(ArcThread thread, final Bindings namespace, final Continuation whatToDo, Pair args) {
          final Input previous = thread.swapStdIn(cast(args.car(), Input.class));
          final Function thunk = cast(args.cdr().car(), Function.class);
          thunk.invoke(thread, namespace, new CallWStdinContinuation(thread, namespace, whatToDo, previous), NIL);
        }
      }, new Builtin("call-w/stdout") {
        public void invoke(ArcThread thread, final Bindings namespace, final Continuation whatToDo, Pair args) {
          final Output previous = thread.swapStdOut(cast(args.car(), Output.class));
          final Function thunk = cast(args.cdr().car(), Function.class);
          thunk.invoke(thread, namespace, new CallWStdoutContinuation(thread, namespace, whatToDo, previous), NIL);
        }
      }, new Builtin("stdin") {
        public void invoke(ArcThread thread, Bindings namespace, Continuation whatToDo, Pair args) {
          whatToDo.eat(thread.stdIn());
        }
      }, new Builtin("stdout") {
        public void invoke(ArcThread thread, Bindings namespace, Continuation whatToDo, Pair args) {
          whatToDo.eat(thread.stdOut());
        }
      }, new Builtin("stderr") {
        public ArcObject invoke(Pair args) {
          return STD_ERR;
        }
      }, new Builtin("disp") {
        public void invoke(ArcThread thread, Bindings namespace, Continuation whatToDo, Pair args) {
          Output out = chooseOutputPort(args.cdr().car(), thread);
          ArcObject o = args.car();
          if (o instanceof ArcString) {
            out.write(((ArcString) o).value());
          } else if (o instanceof ArcCharacter) {
            out.writeChar((ArcCharacter) o);
          } else {
            out.write(o);
          }
          whatToDo.eat(NIL);
        }
      }, new Builtin("write") {
        public void invoke(ArcThread thread, Bindings namespace, Continuation whatToDo, Pair args) {
          chooseOutputPort(args.cdr().car(), thread).write(args.car());
          whatToDo.eat(NIL);
        }
      }, new Builtin("sread") {
        public ArcObject invoke(Pair args, Bindings arc) {
          return cast(args.car(), Input.class).readObject(NIL);
        }
      }, new Builtin("writeb") {
        public void invoke(ArcThread thread, Bindings namespace, Continuation whatToDo, Pair args) {
          chooseOutputPort(args.cdr().car(), thread).writeByte(cast(args.car(), Rational.class));
          whatToDo.eat(NIL);
        }
      }, new Builtin("writec") {
        public void invoke(ArcThread thread, Bindings namespace, Continuation whatToDo, Pair args) {
          chooseOutputPort(args.cdr().car(), thread).writeChar(cast(args.car(), ArcCharacter.class));
          whatToDo.eat(NIL);
        }
      }, new Builtin("readb") {
        public void invoke(ArcThread thread, Bindings namespace, Continuation whatToDo, Pair args) {
          whatToDo.eat(chooseInputPort(args.car(), thread).readByte());
        }
      }, new Builtin("readc") {
        public void invoke(ArcThread thread, Bindings namespace, Continuation whatToDo, Pair args) {
          whatToDo.eat(chooseInputPort(args.car(), thread).readCharacter());
        }
      }, new Builtin("close") {
        public ArcObject invoke(Pair args, Bindings arc) {
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

  private static Output chooseOutputPort(ArcObject port, ArcThread thread) {
    Output out;
    if (!port.isNil()) {
      out = ArcObject.cast(port, Output.class);
    } else {
      out = thread.stdOut();
    }
    return out;
  }

  private static Input chooseInputPort(ArcObject port, ArcThread thread) {
    Input in;
    if (!port.isNil()) {
      in = ArcObject.cast(port, Input.class);
    } else {
      in = thread.stdIn();
    }
    return in;
  }

}
