package rainbow.functions;

import rainbow.Console;
import rainbow.Environment;
import rainbow.types.ArcObject;
import rainbow.types.Pair;
import rainbow.types.Tagged;
import rainbow.types.ArcThreadLocal;

public abstract class ThreadLocals {
  public static void collect(Environment environment) {
    if (!Console.ANARKI_COMPATIBILITY) {
      return;
    }

    environment.add(new Builtin[]{
      new Builtin("thread-local") {
        public ArcObject invoke(Pair args) {
          return new ArcThreadLocal();
        }
      }, new Builtin("thread-local-ref") {
        public ArcObject invoke(Pair args) {
          ArcThreadLocal tl = ArcThreadLocal.cast(args.car(), this);
          return tl.get();
        }
      }, new Builtin("thread-local-set") {
        public ArcObject invoke(Pair args) {
          ArcThreadLocal tl = ArcThreadLocal.cast(args.car(), this);
          ArcObject value = args.cdr().car();
          tl.set(value);
          return value;
        }
      }
    });
  }

}
