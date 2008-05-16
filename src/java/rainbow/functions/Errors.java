package rainbow.functions;

import rainbow.ArcError;
import rainbow.Function;
import rainbow.LexicalClosure;
import rainbow.types.ArcException;
import rainbow.types.ArcObject;
import rainbow.types.ArcString;
import rainbow.types.Pair;
import rainbow.vm.ArcThread;
import rainbow.vm.Continuation;
import rainbow.vm.continuations.ErrorHandler;
import rainbow.vm.continuations.Protector;

public class Errors {
  public static class OnErr extends Builtin {
    public void invoke(ArcThread thread, LexicalClosure lc, Continuation caller, Pair args) {
      final Function errorHandler = cast(args.car(), Function.class);
      final Function tryMe = cast(args.cdr().car(), Function.class);
      tryMe.invoke(thread, lc, new ErrorHandler(thread, lc, caller, errorHandler), NIL);
    }
  }

  public static class Details extends Builtin {
    public ArcObject invoke(Pair args) {
      return cast(args.car(), ArcException.class).message();
    }
  }

  public static class Protect extends Builtin {
    public void invoke(ArcThread thread, LexicalClosure lc, Continuation caller, Pair args) {
      Function during = cast(args.car(), Function.class);
      Function after = cast(args.cdr().car(), Function.class);
      during.invoke(thread, lc, new Protector(thread, lc, caller, after), NIL);
    }
  }

  public static class Err extends Builtin {
    public ArcObject invoke(Pair args) {
      throw new ArcError(cast(args.car(), ArcString.class).value());
    }
  }
}
