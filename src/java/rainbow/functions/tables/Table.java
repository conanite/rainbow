package rainbow.functions.tables;

import rainbow.functions.Builtin;
import rainbow.types.ArcObject;
import rainbow.types.Hash;
import rainbow.types.Pair;
import rainbow.vm.VM;
import rainbow.vm.instructions.PopArg;
import rainbow.Nil;

public class Table extends Builtin {
  public Table() {
    super("table");
  }

  public void invoke(VM vm, Pair args) {
    Hash hash = new Hash();
    vm.pushA(hash);
    if (!(args instanceof Nil)) {
      vm.pushFrame(new PopArg("table-initialiser"));
      ArcObject f = args.car();
      f.invoke(vm, Pair.buildFrom(hash));
    }
  }
}
