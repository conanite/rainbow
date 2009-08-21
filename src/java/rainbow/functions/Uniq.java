package rainbow.functions;

import rainbow.ArcError;
import rainbow.types.ArcObject;
import rainbow.types.Pair;
import rainbow.types.Symbol;
import rainbow.vm.VM;

public class Uniq extends Builtin {
  private static long count = 0;

  public Uniq() {
    super("uniq");
  }

  public void invokef(VM vm) {
    synchronized (getClass()) {
      vm.pushA(Symbol.mkSym("gs" + (++count)));
    }
  }

  public ArcObject invoke(Pair args) {
    try {
      args.mustBeNil();
    } catch (NotNil notNil) {
      throw new ArcError("uniq: expects no args, got " + args);
    }
    synchronized (getClass()) {
      return Symbol.mkSym("gs" + (++count));
    }
  }
}
