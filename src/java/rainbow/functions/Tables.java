package rainbow.functions;

import rainbow.types.*;
import rainbow.Bindings;
import rainbow.Function;
import rainbow.ArcError;
import rainbow.vm.ArcThread;
import rainbow.vm.Continuation;

public class Tables {
  public static class Table extends Builtin {
    public ArcObject invoke(Pair args, Bindings arc) {
      return new Hash();
    }
  }

  public static class MapTable extends Builtin {
    public void invoke(ArcThread thread, Bindings namespace, Continuation whatToDo, Pair args) {
      Function f = (Function) args.car();
      Hash h = (Hash) args.cdr().car();
      h.map(f, thread, namespace, whatToDo);
    }
  }

  public static class Sref extends Builtin {
    public ArcObject invoke(Pair args) {
      if (args.car() instanceof Hash) {
        return srefHash(args);
      } else if (args.car() instanceof ArcString) {
        return srefString(args);
      } else if (args.car() instanceof Pair) {
        return srefList(args);
      }
      throw new ArcError("sref: expects first argument to be a string or a hash or a list");
    }

    private ArcObject srefList(Pair args) {
      Pair target = (Pair) args.car();
      ArcObject newValue = args.cdr().car();
      Rational index = null;
      try {
        index = cast(args.cdr().cdr().car(), Rational.class);
      } catch (ArcError e) {
        System.out.println("trying to sref " + args);
        throw e;
      }
      if (index.toInt() >= target.size()) {
        throw new ArcError("sref: cannot set index " + index + " of list with " + target.size() + " elements");
      }
      target = target.nth(index.toInt());
      target.setCar(newValue);
      return newValue;
    }

    private ArcObject srefString(Pair args) {
      ArcString string = (ArcString) args.car();
      ArcCharacter value = cast(args.cdr().car(), ArcCharacter.class);
      Rational index = cast(args.cdr().cdr().car(), Rational.class) ;
      string.sref(index, value);
      return value;
    }

    private ArcObject srefHash(Pair args) {
      Hash h = (Hash) args.car();
      ArcObject value = args.cdr().car();
      ArcObject key = args.cdr().cdr().car();
      h.sref(key, value);
      return value;
    }
  }
}
