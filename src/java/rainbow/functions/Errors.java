package rainbow.functions;

import rainbow.vm.ArcThread;
import rainbow.vm.Continuation;
import rainbow.vm.continuations.ErrorHandler;
import rainbow.vm.continuations.Protector;
import rainbow.Bindings;
import rainbow.Function;
import rainbow.ArcError;
import rainbow.types.Pair;
import rainbow.types.ArcException;
import rainbow.types.ArcString;
import rainbow.types.ArcObject;

public class Errors {
  public static class OnErr extends Builtin {
    public void invoke(ArcThread thread, Bindings namespace, Continuation whatToDo, Pair args) {
      final Function errorHandler = cast(args.car(), Function.class);
      final Function tryMe = cast(args.cdr().car(), Function.class);
      tryMe.invoke(thread, namespace, new ErrorHandler(thread, namespace, whatToDo, errorHandler), NIL);
    }
  }

  public static class Details extends Builtin {
    public ArcObject invoke(Pair args) {
      return cast(args.car(), ArcException.class).message();
    }
  }

  public static class Protect extends Builtin {
    public void invoke(ArcThread thread, Bindings namespace, Continuation whatToDo, Pair args) {
      Function during = cast(args.car(), Function.class);
      Function after = cast(args.cdr().car(), Function.class);
      during.invoke(thread, namespace, new Protector(thread, namespace, whatToDo, after), NIL);
    }
  }

  public static class Err extends Builtin {
    public ArcObject invoke(Pair args) {
      throw new ArcError(cast(args.car(), ArcString.class).value());
    }
  }
}
