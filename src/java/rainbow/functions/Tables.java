package rainbow.functions;

import rainbow.Function;
import rainbow.LexicalClosure;
import rainbow.types.ArcObject;
import rainbow.types.Hash;
import rainbow.types.Pair;
import rainbow.vm.ArcThread;
import rainbow.vm.Continuation;

public class Tables {
  public static class Table extends Builtin {
    public ArcObject invoke(Pair args) {
      return new Hash();
    }
  }

  public static class MapTable extends Builtin {
    public void invoke(ArcThread thread, LexicalClosure lc, Continuation caller, Pair args) {
      Function f = Builtin.cast(args.car(), "maptable");
      Hash h = Hash.cast(args.cdr().car(), "maptable");
      h.map(f, thread, lc, caller);
    }
  }

  public static class Sref extends Builtin {
    public ArcObject invoke(Pair args) {
      return args.car().sref(Pair.cast(args.cdr(), this));
    }
  }
}
