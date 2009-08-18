package rainbow.vm.compiler;

import rainbow.functions.Macex;
import rainbow.types.ArcObject;
import rainbow.types.Pair;
import rainbow.vm.VM;

public class MacExpander {
  private static final Macex macex = new Macex();

  public static ArcObject expand(VM vm, ArcObject expanded) {
    return macex.invokeAndWait(vm, Pair.buildFrom(expanded));
  }
}
