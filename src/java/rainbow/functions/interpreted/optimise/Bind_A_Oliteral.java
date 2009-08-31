package rainbow.functions.interpreted.optimise;

import rainbow.LexicalClosure;
import rainbow.Nil;
import rainbow.functions.interpreted.InterpretedFunction;
import rainbow.types.ArcObject;
import rainbow.types.Pair;
import rainbow.types.Symbol;
import rainbow.vm.VM;

import java.util.Map;

public class Bind_A_Oliteral extends InterpretedFunction {
  private ArcObject optExpr;
  private InterpretedFunction curried;

  public Bind_A_Oliteral(ArcObject parameterList, Map lexicalBindings, Pair expandedBody) {
    super(parameterList, lexicalBindings, expandedBody);
    optExpr = parameterList.cdr().car().cdr().cdr().car();
    ArcObject optParam = parameterList.cdr().car().cdr().car();
    if (canInline((Symbol) optParam, optExpr)) {
      try {
        curried = (InterpretedFunction) this.curry((Symbol) optParam, optExpr);
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }

  public void invokeN(VM vm, LexicalClosure lc, ArcObject arg) {
    if (curried != null) {
      curried.invokeN(vm, lc, arg);
    } else {
      lc = new LexicalClosure(lexicalBindings.size(), lc);
      lc.add(arg);
      lc.add(optExpr);
      vm.pushFrame(lc, this.instructions);
    }
  }

  public void invokeN(VM vm, LexicalClosure lc, ArcObject arg1, ArcObject arg2) {
    lc = new LexicalClosure(lexicalBindings.size(), lc);
    lc.add(arg1);
    lc.add(arg2);
    vm.pushFrame(lc, this.instructions);
  }

  public void invoke(VM vm, LexicalClosure lc, Pair args) {
    requireNotNil(args, args);
    ArcObject arg1 = args.car();
    ArcObject rest = args.cdr();
    if (rest instanceof Nil) {
      invokeN(vm, lc, arg1);
    } else {
      requireNil(rest.cdr(), args);
      invokeN(vm, lc, arg1, rest.car());
    }
  }
}
