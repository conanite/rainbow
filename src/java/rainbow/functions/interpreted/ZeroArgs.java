package rainbow.functions.interpreted;

import rainbow.types.Pair;
import rainbow.vm.VM;
import rainbow.LexicalClosure;
import rainbow.ArcError;

import java.util.Map;

public class ZeroArgs extends InterpretedFunction {
  public ZeroArgs(Map lexicalBindings, Pair body) {
    super(EMPTY_LIST, lexicalBindings, body);
  }

  public void invoke(VM vm, LexicalClosure lc, Pair args) {
    try {
      args.mustBeNil();
    } catch (NotNil notNil) {
      throw new ArcError("expected 1 arg, got " + args);
    }
    vm.pushFrame(lc, this.instructions);
  }
}
