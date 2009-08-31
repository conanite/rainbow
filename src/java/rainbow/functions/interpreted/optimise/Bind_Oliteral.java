package rainbow.functions.interpreted.optimise;

import rainbow.ArcError;
import rainbow.LexicalClosure;
import rainbow.Nil;
import rainbow.functions.interpreted.InterpretedFunction;
import rainbow.types.ArcObject;
import rainbow.types.Pair;
import rainbow.types.Symbol;
import rainbow.vm.VM;

import java.util.Map;

public class Bind_Oliteral extends InterpretedFunction {
  private ArcObject optExpr;
  private InterpretedFunction curried;

  public Bind_Oliteral(ArcObject parameterList, Map lexicalBindings, Pair expandedBody) {
    super(parameterList, lexicalBindings, expandedBody);
    this.optExpr = parameterList.car().cdr().cdr().car();
    ArcObject optParam = parameterList.car().cdr().car();
    if (canInline((Symbol) optParam, optExpr)) {
      curried = (InterpretedFunction) this.curry((Symbol) optParam, optExpr);
    }
  }

  public void invokeN(VM vm, LexicalClosure lc) {
    if (curried != null) {
      curried.invokeN(vm, lc);
    } else {
      lc = new LexicalClosure(lexicalBindings.size(), lc);
      lc.add(optExpr);
      vm.pushFrame(lc, this.instructions);
    }
  }

  public void invokeN(VM vm, LexicalClosure lc, ArcObject arg) {
    lc = new LexicalClosure(lexicalBindings.size(), lc);
    lc.add(arg);
    vm.pushFrame(lc, this.instructions);
  }

  public void invoke(VM vm, LexicalClosure lc, Pair args) {
    if (args instanceof Nil) {
      invokeN(vm, lc);
    } else {
      try {
        args.cdr().mustBeNil();
      } catch (NotNil notNil) {
        throw new ArcError("expected 0 or 1 args, got extra " + args.cdr() + " calling " + this);
      }
      invokeN(vm, lc, args.car());
    }
  }
}
