package rainbow.functions;

import rainbow.LexicalClosure;
import rainbow.SpecialForm;
import rainbow.types.ArcObject;
import rainbow.types.Pair;
import rainbow.vm.ArcThread;
import rainbow.vm.Continuation;
import rainbow.vm.continuations.IfContinuation;
import rainbow.vm.continuations.MultiSetContinuation;
import rainbow.vm.continuations.QuasiQuoteContinuation;
import rainbow.vm.continuations.SetContinuation;

public class Specials {
  public static class Set extends Builtin implements SpecialForm {
    public void invoke(ArcThread thread, LexicalClosure lc, Continuation caller, final Pair args) {
      if (args.cdr().cdr().isNil()) {
        ArcObject name = args.car();
        ArcObject value = args.cdr().car();
        MultiSetContinuation.MACEX.invoke(thread, lc, new SetContinuation(thread, lc, caller, value), Pair.buildFrom(name));
      } else {
        new MultiSetContinuation(thread, lc, caller, args);
      }
    }
  }

  public static class Quote extends Builtin implements SpecialForm {
    public ArcObject invoke(Pair args) {
      return args.car();
    }
  }

  public static class If extends Builtin implements SpecialForm {
    public void invoke(ArcThread thread, LexicalClosure lc, Continuation caller, Pair args) {
      new IfContinuation(thread, lc, caller, args);
    }
  }

  public static class QuasiQuote extends Builtin implements SpecialForm {
    public void invoke(ArcThread thread, LexicalClosure lc, Continuation caller, Pair args) {
      new QuasiQuoteContinuation(thread, lc, caller, args.car());
    }
  }
}
