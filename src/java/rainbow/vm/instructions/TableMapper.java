package rainbow.vm.instructions;

import rainbow.Nil;
import rainbow.types.ArcObject;
import rainbow.types.Pair;
import rainbow.vm.VM;
import rainbow.vm.Instruction;

public class TableMapper extends Instruction {
  private final ArcObject fn;

  public TableMapper(ArcObject fn) {
    this.fn = fn;
  }

  public void operate(VM vm) {
    Pair list = (Pair) vm.popA();
    if (!(list instanceof Nil)) {
      vm.pushA(list.cdr());
      vm.pushFrame(this);
      Pair args = (Pair) list.car();
      PopArg i = new PopArg("map-table-iterator");
      i.belongsTo(this);
      vm.pushFrame(i);
      fn.invoke(vm, args);
    }
  }

  public String toString() {
    return "(table-mapper " + fn + ")";
  }
}
