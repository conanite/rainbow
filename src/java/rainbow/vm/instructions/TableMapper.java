package rainbow.vm.instructions;

import rainbow.types.ArcObject;
import rainbow.types.Pair;
import rainbow.vm.Instruction;
import rainbow.vm.VM;
import rainbow.Nil;

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
      vm.pushFrame(new PopArg("map-table-iterator"));
      fn.invoke(vm, args);
    }
  }

  public String toString() {
    return "(table-mapper " + fn + ")";
  }
}
