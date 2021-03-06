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

  public void invokef(VM vm, ArcObject f, ArcObject hash) {
    vm.pushA(hash);
    vm.pushA(((Hash) hash).toList());
    TableMapper i = new TableMapper(f);
    i.belongsTo(this);
    vm.pushFrame(i);
  }

  public void invoke(VM vm, Pair args) {
    checkExactArgsCount(args, 2, getClass());
    invokef(vm, args.car(), args.cdr().car());
  }
}
