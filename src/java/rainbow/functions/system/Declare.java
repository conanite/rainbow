package rainbow.functions.system;

import rainbow.functions.Builtin;
import rainbow.types.ArcObject;
import rainbow.types.Pair;
import rainbow.types.Symbol;
import rainbow.vm.VM;
import rainbow.vm.compiler.Compiler;

public class Declare extends Builtin {
  public static final Symbol atstrings = Symbol.mkSym("atstrings");

  public Declare() {
    super("declare");
  }

  public void invokef(VM vm, ArcObject arg1, ArcObject arg2) {
    if (arg1 == atstrings) {
      ArcObject prev = Compiler.atstrings;
      Compiler.atstrings = arg2;
      vm.pushA(prev);
    } else {
      vm.pushA(NIL);
    }
  }

  public void invoke(VM vm, Pair args)  {
    invokef(vm, args.car(), args.cdr().car());
  }
}
