package rainbow.vm.instructions;

import rainbow.types.ArcException;
import rainbow.types.ArcObject;
import rainbow.types.Pair;
import rainbow.vm.VM;
import rainbow.vm.Instruction;

public class Catch extends Instruction implements OnError {
  private ArcObject onerr;
  private int ap;

  public Catch(ArcObject onerr, int ap) {
    this.onerr = onerr;
    this.ap = ap;
  }

  public void operate(VM vm) {
    ArcException error = vm.error();
    if (error == null) {
      return;
    }

    vm.clearError();
    vm.ap(ap);
    onerr.invoke(vm, Pair.buildFrom(error));
  }

  public String toString() {
    return "(catch " + onerr + ")";
  }
}
