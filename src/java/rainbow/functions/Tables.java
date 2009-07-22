package rainbow.functions;

import rainbow.Function;
import rainbow.LexicalClosure;
import rainbow.types.ArcObject;
import rainbow.types.Hash;
import rainbow.types.Pair;
import rainbow.vm.Continuation;
import rainbow.vm.continuations.ResultPassingContinuation;

public class Tables {
  public static class Table extends Builtin {
    @Override
    public void invoke(LexicalClosure lc, Continuation caller, Pair args) {
      Hash hash = new Hash();
      if (args.isNil()) {
        caller.receive(hash);
      } else {
        Function f = Builtin.cast(args.car(), "table");
        f.invoke(lc, new ResultPassingContinuation(caller, hash), Pair.buildFrom(hash));
      }
    }
  }

  public static class MapTable extends Builtin {
    public void invoke(LexicalClosure lc, Continuation caller, Pair args) {
      Function f = Builtin.cast(args.car(), "maptable");
      Hash h = Hash.cast(args.cdr().car(), "maptable");
      h.map(f, lc, caller);
    }
  }

  public static class Sref extends Builtin {
    public ArcObject invoke(Pair args) {
      return args.car().sref(Pair.cast(args.cdr(), this));
    }
  }
}
