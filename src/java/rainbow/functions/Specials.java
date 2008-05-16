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

public class Specials {
  public static class Set extends Builtin implements SpecialForm {
    public void invoke(ArcThread thread, LexicalClosure lc, Continuation caller, final Pair args) {
      new MultiSetContinuation(thread, lc, caller, args).start();
    }
  }

  public static class Quote extends Builtin implements SpecialForm {
    public ArcObject invoke(Pair args) {
      return args.car();
    }
  }

  public static class If extends Builtin implements SpecialForm {
    public void invoke(ArcThread thread, LexicalClosure lc, Continuation caller, Pair args) {
      new IfContinuation(thread, lc, caller, args).start();
    }
  }

  public static class QuasiQuote extends Builtin implements SpecialForm {
    public void invoke(ArcThread thread, LexicalClosure lc, Continuation caller, Pair args) {
      new QuasiQuoteContinuation(thread, lc, caller, args.car()).start();
    }
  }
}
