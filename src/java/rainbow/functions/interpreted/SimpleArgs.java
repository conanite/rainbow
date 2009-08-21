package rainbow.functions.interpreted;

import rainbow.LexicalClosure;
import rainbow.Nil;
import rainbow.types.ArcObject;
import rainbow.types.Pair;
import rainbow.types.Symbol;
import rainbow.vm.VM;

import java.util.Map;

public class SimpleArgs extends InterpretedFunction {

  public SimpleArgs(ArcObject parameterList, Map lexicalBindings, Pair body) {
    super(parameterList, lexicalBindings, body);
  }

  public void invoke(VM vm, LexicalClosure lc, Pair args) {
    lc = new LexicalClosure(lexicalBindings.size(), lc);
    simple(lc, parameterList, args);
    vm.pushFrame(lc, this.instructions);
  }

  private static void simple(LexicalClosure lc, ArcObject parameterList, ArcObject args) {
    while (!(parameterList instanceof Nil)) {
      if (parameterList instanceof Symbol) {
        lc.add(args);
        return;
      } else {
        lc.add(args.car());
        args = args.cdr();
        parameterList = parameterList.cdr();
      }
    }
  }
}
