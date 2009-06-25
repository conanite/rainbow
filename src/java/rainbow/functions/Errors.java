package rainbow.functions;

import rainbow.ArcError;
import rainbow.Function;
import rainbow.LexicalClosure;
import rainbow.types.ArcException;
import rainbow.types.ArcObject;
import rainbow.types.ArcString;
import rainbow.types.Pair;
import rainbow.vm.Continuation;
import rainbow.vm.continuations.ErrorHandler;
import rainbow.vm.continuations.Protector;

public class Errors {
  public static class OnErr extends Builtin {
    public void invoke(LexicalClosure lc, Continuation caller, Pair args) {
      final Function errorHandler = args.car();
      final Function tryMe = args.cdr().car();
      tryMe.invoke(lc, new ErrorHandler(lc, caller, errorHandler), NIL);
    }
  }

  public static class Details extends Builtin {
    public ArcObject invoke(Pair args) {
      return ArcException.cast(args.car(), this).message();
    }
  }

  public static class Protect extends Builtin {
    public void invoke(LexicalClosure lc, Continuation caller, Pair args) {
      Function during = args.car();
      Function after = args.cdr().car();
      during.invoke(lc, new Protector(lc, caller, after), NIL);
    }
  }

  public static class Err extends Builtin {
    public ArcObject invoke(Pair args) {
      throw new ArcError(ArcString.cast(args.car(), this).value());
    }
  }
}
