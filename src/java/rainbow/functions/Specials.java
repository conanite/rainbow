package rainbow.functions;

import rainbow.SpecialForm;
import rainbow.Bindings;
import rainbow.types.Pair;
import rainbow.types.ArcObject;
import rainbow.vm.ArcThread;
import rainbow.vm.Continuation;
import rainbow.vm.continuations.MultiSetContinuation;
import rainbow.vm.continuations.IfContinuation;
import rainbow.vm.continuations.FunctionBodyBuilder;
import rainbow.vm.continuations.QuasiQuoteContinuation;

public class Specials {
  public static class Set extends Builtin implements SpecialForm {
    public void invoke(ArcThread thread, final Bindings namespace, final Continuation whatToDo, final Pair args) {
      new MultiSetContinuation(thread, namespace, whatToDo, args).start();
    }
  }

  public static class Quote extends Builtin implements SpecialForm {
    public ArcObject invoke(Pair args, Bindings arc) {
      return args.car();
    }
  }

  public static class If extends Builtin implements SpecialForm {
    public void invoke(final ArcThread thread, final Bindings namespace, final Continuation whatToDo, Pair args) {
      new IfContinuation(thread, namespace, whatToDo, args).start();
    }
  }

  public static class Fn extends Builtin implements SpecialForm {
    public void invoke(ArcThread thread, Bindings namespace, Continuation whatToDo, Pair args) {
      new FunctionBodyBuilder(thread, namespace, whatToDo, args).start();
    }
  }

  public static class QuasiQuote extends Builtin implements SpecialForm {
    public void invoke(ArcThread thread, Bindings namespace, Continuation whatToDo, Pair args) {
      new QuasiQuoteContinuation(thread, namespace, whatToDo, args.car(), this).start();
    }
  }
}
