package rainbow.functions.tables;

import rainbow.functions.Builtin;
import rainbow.types.ArcObject;
import rainbow.types.Hash;
import rainbow.types.Pair;
import rainbow.vm.VM;
import rainbow.vm.instructions.TableMapper;

public class MapTable extends Builtin {
  public MapTable() {
    super("maptable");
  }

  public void invoke(VM vm, Pair args) {
    ArcObject f = args.car();
    Hash h = (Hash) args.cdr().car();
    vm.pushA(h);
    vm.pushA(h.toList());
    vm.pushFrame(new TableMapper(f));
  }
}
