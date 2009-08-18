package rainbow.vm.interpreter;

import rainbow.functions.InterpretedFunction;
import rainbow.functions.InterpretedFunction.ZeroArgs;
import rainbow.types.ArcObject;
import rainbow.types.Pair;
import rainbow.types.Symbol;
import rainbow.vm.instructions.invoke.*;

import java.util.List;

public class Invocation extends ArcObject {
  private final Pair parts;

  public Invocation(Pair parts) {
    this.parts = parts;
  }

  public ArcObject type() {
    return Symbol.mkSym("function-invocation");
  }

  public String toString() {
    return parts.toString();
  }

  public void addInstructions(List i) {
    if (parts.len() == 1L && parts.car() instanceof ZeroArgs) {
      ((InterpretedFunction) parts.car()).instructions().copyTo(i);
      return;
    }

    switch ((int) parts.len()) {
      case 1:
        Invoke_0.addInstructions(i,
                parts.car());
        break;
      case 2:
        Invoke_1.addInstructions(i,
                parts.car(),
                parts.cdr().car()
        );
        break;
      case 3:
        Invoke_2.addInstructions(i,
                parts.car(),
                parts.cdr().car(),
                parts.cdr().cdr().car()
        );
        break;
      case 4:
        Invoke_3.addInstructions(i,
                parts.car(),
                parts.cdr().car(),
                parts.cdr().cdr().car(),
                parts.cdr().cdr().cdr().car()
        );
        break;
      default:
        Invoke_N.addInstructions(i,
                parts.car(),
                (Pair) parts.cdr());
    }
  }
}
